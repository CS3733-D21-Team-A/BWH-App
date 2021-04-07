package edu.wpi.aquamarine_axolotls;

public class Adb {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println(
				"1 - Node Information\n"
				+ "2 - Update Node Coordinates\n"
				+ "3 - Update Node Location Long Name\n"
				+ "4 - Edge Information\n"
				+ "5 - Exit Program"
			);
			return;
		}

		DatabaseService db = new DatabaseService();

		switch(Integer.parseInt(args[0])) {
			case 1:
				db.getNodeTable().nodeInformation();
				break;
			case 2:
				db.getNodeTable().updateNodeCoords();
				break;
			case 3:
				db.getNodeTable().updateNodeLocationName();
				break;
			case 4:
				db.getEdgeTable().edgeInformation();
				break;
		}
	}
}


