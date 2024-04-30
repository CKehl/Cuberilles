typedef struct _data_t
{
	float x;
	int y;
} data_t;
#define getKey(a) ((a).x)
#define getValue(a) ((a).y)

kernel void computeDistance(global float3 * vertex, global float * pointDistance, int size, float cx, float cy, float cz) 
{
    unsigned int x = get_global_id(0);

	if(x<size)
	{
		float3 view = (float3)(cx,cy,cz);
		pointDistance[x] = distance(vertex[x], view);
	}
}

kernel void sort(global float * pointDistance, global int * outIndices, int size, __local data_t * aux) 
{
	int i = get_local_id(0); // index in workgroup
	int wg = get_local_size(0); // workgroup size = block size, power of 2

	// Move IN, OUT to block start
	int offset = get_group_id(0) * wg;
	
	if(i<size)
	{
		aux[i].x = pointDistance[i];
		aux[i].y = i;
	}
	else
	{
		aux[i].x = FLT_MAX;
		aux[i].y = 0;
	}
	barrier(CLK_LOCAL_MEM_FENCE); // make sure AUX is entirely up to date
	
	
	// Now we will merge sub-sequences of length 1,2,...,WG/2
	for (int length=1;length<wg;length<<=1)
	{
		data_t iData = aux[i];
		uint iKey = getKey(iData);
		int ii = i & (length-1);  // index in our sequence in 0..length-1
		int sibling = (i - ii) ^ length; // beginning of the sibling sequence
		int pos = 0;
		for (int inc=length;inc>0;inc>>=1) // increment for dichotomic search
		{
			int j = sibling+pos+inc-1;
			uint jKey = getKey(aux[j]);
			bool smaller = (jKey < iKey) || ( jKey == iKey && j < i );
			pos += (smaller)?inc:0;
			pos = min(pos,length);
		}
		int bits = 2*length-1; // mask for destination
		int dest = ((ii + pos) & bits) | (i & ~bits); // destination index in merged sequence
		barrier(CLK_LOCAL_MEM_FENCE);
		aux[dest] = iData;
		barrier(CLK_LOCAL_MEM_FENCE);
	}
	
	// Write output
	if(i<size)
	{
		outIndices[i] = aux[i].y;
	}
}