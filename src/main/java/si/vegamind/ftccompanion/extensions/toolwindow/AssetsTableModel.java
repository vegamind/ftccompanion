package si.vegamind.ftccompanion.extensions.toolwindow;

import lombok.Getter;
import lombok.Setter;
import si.vegamind.ftccompanion.models.FtcAsset;

import javax.swing.table.AbstractTableModel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AssetsTableModel extends AbstractTableModel {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy, h:mm:ss a");

	private final String[] columnNames = {"Name", "Date Modified"};
	@Getter
	@Setter
	private FtcAsset[] assets;

	public AssetsTableModel(FtcAsset[] assets) {
		this.assets = assets;
	}

	@Override
	public int getRowCount() {
		return assets.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) {
			return assets[rowIndex].getName();
		} else {
			long millis = assets[rowIndex].getDateModifiedMillis();
			LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());

			return dateTime.format(formatter);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
}