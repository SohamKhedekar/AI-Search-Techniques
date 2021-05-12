import java.util.*;
import java.io.*;

class homework {
	static class IODetails {
		String algo;
		int[] dimensions = new int[3];
		String entry;
		String exit;
		int N = 0;
		HashMap<String, List<Integer>> gridValuesToActionMap = new HashMap<>();
		int[][] actionDetails = {
			{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}, 
			{1, 1, 0}, {1, -1, 0}, {-1, 1, 0}, {-1, -1, 0}, {1, 0, 1}, {1, 0, -1}, {-1, 0, 1},
			{-1, 0, -1}, {0, 1, 1}, {0, 1, -1}, {0, -1, 1}, {0, -1, -1}
		};
		HashMap<String, String> visited = new HashMap<>();
		HashMap<String, Integer> cost = new HashMap<>();
	}

	public static void main (String args[]) {
		File file = new File("input.txt");
		IODetails obj = new IODetails();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			obj.algo = br.readLine();
			if (obj.algo == null) {
				File outputFile = new File("output.txt");
				PrintWriter writer = new PrintWriter("output.txt");
				writer.print("");
				return;
			}
			obj.dimensions = getCoords(br.readLine());
			obj.entry = br.readLine();
			obj.exit = br.readLine();
			obj.N = Integer.parseInt(br.readLine());
			for (int i = 0; i < obj.N; i++) {
				String gridDetails = br.readLine();
				List<Integer> actionList = new ArrayList<>();
				if (gridDetails == null) {
					break;
				}
				String[] gridDetailsActions = gridDetails.split(" ");
				for (int k = 3; k < gridDetailsActions.length; k++) {
					if (gridDetailsActions[k].length() > 0) {
						actionList.add(Integer.parseInt(gridDetailsActions[k])-1);
					}
				}
				StringBuilder temp = new StringBuilder();
				temp.append(gridDetailsActions[0]);
				temp.append(" ");
				temp.append(gridDetailsActions[1]);
				temp.append(" ");
				temp.append(gridDetailsActions[2]);
				obj.gridValuesToActionMap.put(temp.toString(), actionList);
			}
			if (!obj.gridValuesToActionMap.containsKey(obj.entry))
				obj.gridValuesToActionMap.put(obj.entry, new ArrayList<>());
			if (!obj.gridValuesToActionMap.containsKey(obj.exit))
				obj.gridValuesToActionMap.put(obj.exit, new ArrayList<>());
			if (obj.algo.equals("BFS"))
				bfs(obj);
			if (obj.algo.equals("UCS"))
				ucs(obj);
			if (obj.algo.equals("A*"))
				a_star(obj);
			getOutput(obj);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	static int[] getCoords (String temp) {
		String[] cString = temp.split(" ");
		int[] cInt = new int[3];
		for (int i = 0; i < 3; i++) {
			cInt[i] = Integer.parseInt(cString[i]);
		}
		return cInt;
	}

	static void getOutput (IODetails obj) {
		String gridValueS = obj.exit;
		Stack<String> valueStack = new Stack<>();
		Stack<Integer> costStack = new Stack<>();
		if (obj.cost.containsKey(obj.exit)) {
			while (!gridValueS.equals(obj.entry)) {
				valueStack.push(gridValueS);
				costStack.push(obj.cost.get(gridValueS) - obj.cost.get(obj.visited.get(gridValueS)));
				gridValueS = obj.visited.get(gridValueS);
			}
			valueStack.push(gridValueS);
			costStack.push(0);
		}
		File file = new File("output.txt");
		try {
			PrintWriter writer = new PrintWriter("output.txt");
			if (obj.cost.containsKey(obj.exit)) {
				writer.println(obj.cost.get(obj.exit));
				writer.println(valueStack.size());
				while (!valueStack.isEmpty()) {
					writer.print(valueStack.pop());
					writer.print(" ");
					writer.println(costStack.pop());
				}
			}
			else {
				writer.println("FAIL");
			}
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}

	static void bfs (IODetails obj) {
		Queue<String> q = new LinkedList<>();
		obj.visited.put(obj.entry, "");
		obj.cost.put(obj.entry, 0);
		q.add(obj.entry);
		while (!q.isEmpty()) {
			String gridValueS = q.poll();
			int[] gridValueI = getCoords(gridValueS);
			List<Integer> actionList = obj.gridValuesToActionMap.get(gridValueS);
			for (int i:actionList) {
				StringBuilder temp = new StringBuilder();
				temp.append(Integer.toString(gridValueI[0]+obj.actionDetails[i][0]));
				temp.append(" ");
				temp.append(Integer.toString(gridValueI[1]+obj.actionDetails[i][1]));
				temp.append(" ");
				temp.append(Integer.toString(gridValueI[2]+obj.actionDetails[i][2]));
				if (obj.gridValuesToActionMap.containsKey(temp.toString()) && !obj.visited.containsKey(temp.toString())) {
					obj.visited.put(temp.toString(), gridValueS);
					obj.cost.put(temp.toString(), obj.cost.get(gridValueS)+1);
					q.add(temp.toString());
					if (temp.equals(obj.exit)) {
						break;
					}
				}
			}
		}
	}

	static void ucs (IODetails obj) {
		PriorityQueue<String> pq = new PriorityQueue<>((a, b)->obj.cost.get(a)-obj.cost.get(b));
		obj.visited.put(obj.entry, "");
		obj.cost.put(obj.entry, 0);
		pq.add(obj.entry);
		while (!pq.isEmpty()) {
			String gridValueS = pq.poll();
			int[] gridValueI = getCoords(gridValueS);
			List<Integer> actionList = obj.gridValuesToActionMap.get(gridValueS);
			for (int i:actionList) {
				StringBuilder temp = new StringBuilder();
				temp.append(Integer.toString(gridValueI[0]+obj.actionDetails[i][0]));
				temp.append(" ");
				temp.append(Integer.toString(gridValueI[1]+obj.actionDetails[i][1]));
				temp.append(" ");
				temp.append(Integer.toString(gridValueI[2]+obj.actionDetails[i][2]));
				int costToBeAdded = 0;
				if (i <= 5) costToBeAdded = 10;
				else costToBeAdded = 14;
				if (obj.gridValuesToActionMap.containsKey(temp.toString()) && 
					(!obj.visited.containsKey(temp.toString()) || obj.cost.get(temp.toString()) > obj.cost.get(gridValueS)+costToBeAdded)) {
					obj.visited.put(temp.toString(), gridValueS);
					obj.cost.put(temp.toString(), obj.cost.get(gridValueS)+costToBeAdded);
					pq.add(temp.toString());
				}
			}
		}
	}

	static void a_star (IODetails obj) {
		HashMap<String, Integer> heuristicCost = new HashMap<>();
		PriorityQueue<String> pq = new PriorityQueue<>((a, b)->(obj.cost.get(a)+heuristicCost.get(a))-(obj.cost.get(b)+heuristicCost.get(b)));
		int[] exitGridValueI = getCoords(obj.exit);
		obj.visited.put(obj.entry, "");
		obj.cost.put(obj.entry, 0);
		heuristicCost.put(obj.entry, calcHC(obj.entry, exitGridValueI));
		pq.add(obj.entry);
		while (!pq.isEmpty()) {
			String gridValueS = pq.poll();
			int[] gridValueI = getCoords(gridValueS);
			List<Integer> actionList = obj.gridValuesToActionMap.get(gridValueS);
			for (int i:actionList) {
				StringBuilder temp = new StringBuilder();
				temp.append(Integer.toString(gridValueI[0]+obj.actionDetails[i][0]));
				temp.append(" ");
				temp.append(Integer.toString(gridValueI[1]+obj.actionDetails[i][1]));
				temp.append(" ");
				temp.append(Integer.toString(gridValueI[2]+obj.actionDetails[i][2]));
				int costToBeAdded = 0;
				if (i <= 5) costToBeAdded = 10;
				else costToBeAdded = 14;
				if (obj.gridValuesToActionMap.containsKey(temp.toString()) && 
					(!obj.visited.containsKey(temp.toString()) || obj.cost.get(temp.toString()) > obj.cost.get(gridValueS)+costToBeAdded)) {
					obj.visited.put(temp.toString(), gridValueS);
					obj.cost.put(temp.toString(), obj.cost.get(gridValueS)+costToBeAdded);
					heuristicCost.put(temp.toString(), calcHC(temp.toString(), exitGridValueI));
					pq.add(temp.toString());
				}
			}
		}
	}

	static int calcHC (String temp, int[] exitGridValueI) {
		int[] currentGridValueI = getCoords(temp);
		return (int)Math.pow(currentGridValueI[0] - exitGridValueI[0], 2) + (int)Math.pow(currentGridValueI[1] - exitGridValueI[1], 2)
		 + (int)Math.pow(currentGridValueI[2] - exitGridValueI[2], 2);
	}
}




