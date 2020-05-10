package model;

import Jama.Matrix;
import org.eclipse.swt.graphics.Point;
import ui.Window;

public class Lab2Model {
	private Window parent;
	private Matrix points;//точки через которые должна проходить кривая
	private Matrix tangents0N;//касательные к первой и последней точке
	private Matrix tangents;//касательные
	private Matrix pointsOut;
	private int steps = 50;

	public Lab2Model(Window parent){
		this.parent = parent;
	}

	public void run(){
		// Интерполяция 5 точек нормализованным кубическим сплайном, размерность = 2 (плоскость).

		//1. Инициализация переменных, сброс.
		//2. Вычисление касательных к точкам с 1-й по N-1-ую: P' = M^-1 * R.
		//	Предварительное вычисление матриц - для уменьшения затрат памяти:
		//3. Вычисление матриц F = T * N для всех t = 0 1/step 2/step ... 1;
		//	T = [t^3 t^2 t 1], N - матрица коэффициентов.
		//4. Вычисление матриц G для всех k = 0 1 ... N-1; G = [Pk Pk+1 Pk' Pk+1']'
		//5. Вычисление координат всех точек:
		//	for(int k = 1; k <= N-1; k++)
		//	for(double t = 0; t <= 1; t+= step){
		//		Point[k*step + t] = F[t] * G[k],
		//	}
		//6.вызов отрисовщика.

		//1. Инициализация переменных, random, сброс.
		//количество точек
		int pointsCount = 5;
		int dimension = 2;
		//случайные числа от 0 до  размер окна*0.9
		int[] upperRandomLimits = new int[dimension];
		int[] lowerRandomLimits = new int[dimension];
//		randomLimits[0] = parent.getShell().getSize().x - 300;
//		randomLimits[1] = parent.getShell().getSize().y - 300;
		upperRandomLimits[0] = (int) (parent.getShell().getSize().x*0.8);
		upperRandomLimits[1] = (int) (parent.getShell().getSize().y*0.8);
		lowerRandomLimits[0] = (int) (parent.getShell().getSize().x*0.14);
		lowerRandomLimits[1] = (int) (parent.getShell().getSize().y*0.14);
		int xTangentRandomLimit = 500, yTangentRandomLimit = 500;// при больших векторах касательных на концах сплайн нормальный, при малых - с малыми закруглениями у этих концов, иногда касательная и отрезок у конца смотрят в разные стороны
		points = new Matrix(pointsCount,dimension);
		for(int i = 0; i < pointsCount; i++){
			for (int j = 0; j < dimension; j++){
				points.set(i,j,(int)((Math.random() * (upperRandomLimits[j] - lowerRandomLimits[j])) + lowerRandomLimits[j]));
			}
		}
		System.out.println("random points");
		points.print(4, 3);
		//первая и последняя касательные
		tangents0N = new Matrix(new double[][] {{(Math.random()*2 - 1) * xTangentRandomLimit,(Math.random()*2 - 1) * yTangentRandomLimit},{(Math.random()*2 - 1) * xTangentRandomLimit,(Math.random()*2 - 1) * yTangentRandomLimit}});
		System.out.println("P0 & PN");
		tangents0N.print(2, 2);

		Matrix[] result = computeCubeSplineAndGetTangents(points,tangents0N,steps);
		pointsOut = result[0];
		tangents = result[1];

		//	Все!
	}

	public Matrix getOutPoints(){
		return pointsOut;
	}

	public Matrix getPoints(){
		return points;
	}
	public Matrix getTangents(){
		return tangents;
	}
	/**
	 * Интерполяция 5 точек нормализованным кубическим сплайном,
	 * размерность = 2 или больше (определяется по матрице точек).
	 * 1. Инициализация переменных, сброс.
	 * 2. Вычисление касательных к точкам с 1-й по N-1-ую: P' = M^-1 * R.
	 * 	Предварительное вычисление матриц - для уменьшения затрат памяти:
	 * 3. Вычисление матриц F = T * N для всех t = 0 1/step 2/step ... 1;
	 * 	T = [t^3 t^2 t 1], N - матрица коэффициентов.
	 * 4. Вычисление матриц G для всех k = 0 1 ... N-1; G = [Pk Pk+1 Pk' Pk+1']'
	 * 5. Вычисление координат всех точек:
	 * 	for(int k = 1; k <= N-1; k++)
	 * 	for(double t = 0; t <= 1; t+= step){
	 * 		Point[k*step + t] = F[t] * G[k],
	 * 	}
	 * @param points точки
	 * @param tangents0andN матрица с касательными: |к первой точке &nbsp&nbsp&nbsp&nbsp&nbsp||к последней точке|
	 * @param steps число точек сплайна для каждого отрезка между соседними точками
	 * @return	массив из 2-х матриц: первая - результирующие точки, вторая - касательные ко всем точкам
	 */
	private static Matrix[] computeCubeSplineAndGetTangents(Matrix points, Matrix tangents0andN, int steps){
		//2. Вычисление касательных к точкам с 1-й по N-1-ую: P' = M^-1 * R.
		//матрица M = 0 .........  0
//					  1 4 1 0 ...  0
//					  0 1 4 1 0 .. 0
//					  ......1 4 1...
//					  0........1 4 1
//					  0 .......0 0 1
		boolean debug = false;
		int dimension = points.getColumnDimension();
		int pointsCount = points.getRowDimension();
		Matrix M = new Matrix(pointsCount, pointsCount);
		M.set(0, 0, 1.0);
		M.set(pointsCount-1, pointsCount-1, 1.0);
		Matrix temp = new Matrix(new double[] {1.0, 4.0, 1.0}, 1);
		for (int i = 1; i < pointsCount -1 ; i++){
			M.setMatrix(new int[] {i}, i - 1, i + 1, temp);
		}
		if (debug){
			System.out.println("M:");
			M.print(4, 1);
			M.inverse().print(4, 1);
		}

		Matrix R = new Matrix(pointsCount, dimension);
//		R.set(0,0,tangent0.x);
//		R.set(0,1,tangent0.y);
//		R.set(pointsCount - 1,0,tangentN.x);
//		R.set(pointsCount - 1,1,tangentN.y);
		R.setMatrix(new int[]{0, pointsCount - 1},0,dimension - 1,tangents0andN);
		for (int i = 0; i < pointsCount - 2 ; i++){
//			R.setMatrix(new int[] {i + 1}, 0, dimension - 1, (  points.getMatrix(new int[] {i + 2}, 0, 1).minus(points.getMatrix(new int[] {i + 1}, 0, 1))  ).plus(  (points.getMatrix(new int[] {i + 1}, 0, 1).minus(points.getMatrix(new int[] {i}, 0, 1)))  ).times(3.0) );
			R.setMatrix(new int[] {i + 1}, 0, dimension - 1, points.getMatrix(new int[] {i + 2}, 0, dimension - 1).minus(points.getMatrix(new int[] {i}, 0, dimension - 1)).times(3.0) );//убраны ненужные слагаемые +Pk-1 - Pk-1
//			R.set(i + 1, 1, 3*((points[i + 2].y - points[i + 1].y) + (points[i + 1].y - points[i].y)));
		}
		if (debug) {
			System.out.println("R:");
			R.print(4, 1);
		}

		Matrix tangents = M.inverse().times(R);
		if (debug) {
			System.out.println("P':");
			tangents.print(4, 3);
		}
		//	Предварительное вычисление матриц - для уменьшения затрат памяти:
		//3. Вычисление матриц F = T * N для всех t = 0 1/step 2/step ... 1;
		//	T = [t^3 t^2 t 1], N - матрица коэффициентов.
		Matrix N = new Matrix(new double[][] {{2.0, -2.0, 1.0, 1.0},{-3.0, 3.0, -2.0, -1.0},{0.0, 0.0, 1.0, 0.0},{1.0, 0.0, 0.0, 0.0}});
		Matrix[] F = new Matrix[steps];
		double step = 1/(double)steps;
		double t = 0;
		for(int i = 0; i < steps; i++){
			t = step * i;
			F[i] = new Matrix(new double[][] {{t*t*t, t*t, t, 1.0}}).times(N);
			if (debug){
				System.out.println("t: " + t + " step: " + step + " i: " + i);
				System.out.println("F" + i + ":");
				F[i].print(4, 1);
			}
		}

		//4. Вычисление матриц G для всех k = 0 1 ... N-1; G = [Pk Pk+1 Pk' Pk+1']'
		Matrix[] G = new Matrix[pointsCount - 1];
		for(int k = 0; k < pointsCount - 1; k++){
//			G[k] = new Matrix(new double[][] {{points[k].x, points[k].y}, {points[k+1].x, points[k+1].y}, {tangents.get(k, 0), tangents.get(k, 1)}, {tangents.get(k, 1)}}).times(N);
			G[k] = new Matrix(4, dimension);
			G[k].setMatrix(0, 1, 0, dimension - 1, points.getMatrix(k, k + 1, 0, dimension - 1));//[Pk Pk+1
			G[k].setMatrix(2, 3, 0, dimension - 1, tangents.getMatrix(k, k + 1, 0, dimension - 1));//Pk' Pk+1']'
			if (debug){
				System.out.println("G" + k + ":");
				G[k].print(4, 1);
			}
		}

		//5. Вычисление координат всех точек:
		//	for(int k = 1; k <= N-1; k++)
		//	for(double t = 0; t <= 1; t+= step){
		//		Point[k*step + t] = F[t] * G[k],
		//	}
		Matrix pointsOut = new Matrix((pointsCount - 1)*steps + 1, dimension);
		for(int k = 0; k < pointsCount - 1; k++){
			for(int i = 0; i < steps; i++){
				pointsOut.setMatrix(new int[] {k*steps + i}, 0, dimension - 1, F[i].times(G[k]));
			}
		}
		pointsOut.setMatrix(new int[] {(pointsCount - 1)*steps}, 0, dimension - 1, points.getMatrix(new int[]{pointsCount - 1}, 0, dimension - 1));//последняя точка
		if (debug) pointsOut.print(4, 2);
		Matrix[] result = new Matrix[2];
		result[0] = pointsOut;
		result[1] = tangents;
		return result;
	}

	/**
	 * Интерполяция 5 точек нормализованным кубическим сплайном, размерность = 2 или больше (определяется по матрице точек).
	 * 1. Инициализация переменных, сброс.
	 * 2. Вычисление касательных к точкам с 1-й по N-1-ую: P' = M^-1 * R.
	 * 	Предварительное вычисление матриц - для уменьшения затрат памяти:
	 * 3. Вычисление матриц F = T * N для всех t = 0 1/step 2/step ... 1;
	 * 	T = [t^3 t^2 t 1], N - матрица коэффициентов.
	 * 4. Вычисление матриц G для всех k = 0 1 ... N-1; G = [Pk Pk+1 Pk' Pk+1']'
	 * 5. Вычисление координат всех точек:
	 * 	for(int k = 1; k <= N-1; k++)
	 * 	for(double t = 0; t <= 1; t+= step){
	 * 		Point[k*step + t] = F[t] * G[k],
	 * 	}
	 * @param points точки
	 * @param tangents0andN матрица с касательными: |к первой точке &nbsp&nbsp&nbsp&nbsp&nbsp||к последней точке|
	 * @param steps число точек сплайна для каждого отрезка между соседними точками
	 * @return	матрица с координатами точек сплайна
	 */
	public static Matrix computeCubeSpline(Matrix points, Matrix tangents0andN, int steps){
		return computeCubeSplineAndGetTangents(points, tangents0andN, steps)[0];
	}
}
