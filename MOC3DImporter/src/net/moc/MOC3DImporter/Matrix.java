package net.moc.MOC3DImporter;

public class Matrix {

	private float[] _data;
	public float[] getData() { return this._data; }
	public void setData(float[] data) { this._data = data; }

	public int rows;
	public int columns;

	//Create 3x3 matrix by default
	public Matrix() { this(3); }

	//Create square matrix
	public Matrix(int numRows) {
		rows = numRows;
		columns = numRows;
		init();

	}

	//Create matrix
	public Matrix(int numRows, int numColumns) {
		rows = numRows;
		columns = numColumns;
		init();

	}

	private void init() {
		_data = new float[rows * columns];

		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < columns; ++j)
				set(i, j, ((i == j) ? 1 : 0));

	}

	public void set(float value) {
		for(int i=0 ; i<rows ; ++i)
			for(int j=0 ; j<columns ; ++j)
				set(i,j, value);

	}

	public Matrix clone() {
		Matrix m = new Matrix(rows, columns);

		for(int i=0 ; i<rows ; ++i)
			for(int j=0 ; j<columns ; ++j)
				m.set(i,j,get(i,j));

		return m;

	}

	public float get(int i, int j) { return _data[i*columns+j]; }

	public void set(int i, int j, float value) { _data[i*columns + j] = value; }

	public Matrix add(Matrix r) {
		Matrix m = new Matrix(rows, columns);

		for(int i=0;i<rows;++i)
			for(int j=0;j<columns;++j)
				m.set(i, j, get(i, j) + r.get(i, j));

		return m;

	}


	public void addEq(Matrix r)	{
		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < columns; ++j)
				set(i, j, get(i, j) + r.get(i, j));

	}

	public Matrix sub(Matrix r)	{
		Matrix m = new Matrix(rows, columns);

		for(int i=0;i<rows;++i)
			for(int j=0;j<columns;++j)
				m.set(i, j, get(i, j) - r.get(i, j));

		return m;

	}

	public Matrix mul(Matrix r) {
		Matrix m = new Matrix(rows, r.columns);

		if(columns == r.rows) {
			for(int i=0;i<rows;++i) {
				for(int j=0;j<r.columns;++j) {
					float val = 0;
					for(int index=0;index<columns;++index)
						val += (get(i, index) * r.get(index, j));
					m.set(i,j,val);
					
				}
				
			}
			
		}
		//if the two matrices are not fit for multiplication
		//return an identity matrix
		return m;

	}

	public Matrix mul(float v) {
		Matrix m = this.clone();

		for(int i=0 ; i<rows ; ++i)
			for(int j=0 ; j<columns ; ++j)
				m.set(i, j, get(i, j) * v);

		return m;

	}

	public void mulEq(float v) {
		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < columns; ++j)
				set(i, j, get(i, j) * v);

	}

	public Matrix transpose() {
		Matrix m = new Matrix(this.columns, this.rows);

		for(int i=0;i<m.rows;++i)
			for(int j=i;j<m.columns;++j)
				m.set(i, j, get(j, i));

		return m;

	}

	//Uses the Gaussian Elimination Method
	public Matrix inverse() {
		//initially identity
		Matrix result = new Matrix(rows, columns);

		//temporary copy of this matrix where elementary row operations will be performed.
		Matrix m = this.clone();

		int row, col, index;

		// Try to make m into the identity matrix.  Perform corresponding
		// operations on result;
		for (row=0; row<rows; ++row) {
			int pivot_row = row;
			// Find the best pivot (i.e. largest magnitude)
			for ( index=row+1; index<rows; ++index)
				if (Math.abs(m.get(index, row)) > Math.abs(m.get(pivot_row, row)))
					pivot_row = index;

			// Swap rows to put the best pivot on the diagonal.
			for (index=0; index<columns; ++index) {
				float temp;

				temp = m.get(row, index);
				m.set(row, index, m.get(pivot_row, index));
				m.set(pivot_row,index,temp);

				temp = result.get(row, index);
				result.set(row, index, result.get(pivot_row, index));
				result.set(pivot_row,index,temp);
			}

			// Normalize the pivot row (i.e. put a one on the diagonal)
			float pivot = m.get(row, row);
			for (col=0; col<columns; ++col) {
				m.set(row, col, m.get(row, col) / pivot);
				result.set(row, col, result.get(row, col) / pivot);
				
			}		

			// Introduce zeros above and below the pivot.
			for (index=0; index<rows; ++index) {
				if(index != row) {
					float scale = m.get(index, row);
					for (col=0; col<columns; ++col) {
						m.set(index, col, m.get(index, col) - scale * m.get(row, col));
						result.set(index, col, result.get(index, col) - scale * result.get(row, col));
						
					}

				}
				
			}

		}

		return result;
	}

	public String ToString() {
		String str = "";

		for (int i = 0; i < rows; ++i)
		{
			str += "\nrow(" + i + "): ";
			for (int j = 0; j < columns; ++j)
				str += get(i, j) + ",";
		}

		return str;
	}

}


