package eg.edu.alexu.csd.oop.jdbc.cs32.model;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class MetaData implements ResultSetMetaData{
	private List<String> Coulmun;
	private List<String> Types;
	private String TableName;
	private Object[][] Data;
	public MetaData(List<String> coulmun,List<String> types, String tableName, Object[][] data) {
		this.Coulmun = coulmun;
		this.Types = types;
		this.TableName = tableName;
		this.Data = data;
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnCount() throws SQLException {
		return this.Data[0].length;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		return this.Coulmun.get(column - 1);
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		return this.Coulmun.get(column - 1);
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		int check = -1;
		switch (this.Types.get(column - 1)) {
		case "varchar":
			check = 0;
			break;
		case "int":
			check = 1;
			break;
		default:
			break;
		}
		return check;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getScale(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTableName(int column) throws SQLException {
		if (column > 0 && column < this.Coulmun.size()) {
			return this.TableName;
		}
		return null;
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int isNullable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
