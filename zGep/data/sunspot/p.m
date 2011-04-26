(* 准备太阳黑子微分方程预测数据 *)
ep[] := Module[{},
	formula = Sin[t]/E^(t/4);
	tt = Table[{t->a}, {a, 0.0, 10-0.1, 0.1}];
	n = Length[tt];

	stand = D[formula, t] /. tt;
	stand = Drop[stand, 5];
	stand = Drop[stand, -15];

	tt2 = Drop[tt, 5];
	tt2 = Drop[tt2, -15];

	Export["stand.txt", {t/.tt2, stand}//Transpose, "Table"];
	g1 = ListPlot[stand, PlotJoined->True, PlotRange->{-0.85, 0.85}];

	noise = formula /. tt;
	noise = Table[noise[[x]] + noise[[x]]*Random[Real, {-0.04, 0.04}], {x, 1, n}];

	aaaa[noise, 1, 1, 5, 0.0, 0.1];
	d2 = y1;
	Export["nofilter.txt", {t/.tt2, y1}//Transpose, "Table"];
	g = Show[{g1, g2}];
	Export["a1.eps", g, "EPS"];

	aaaa[noise, 10, 0.3, 5, 0.0, 0.1];
	d3 = y1;
	Export["filter.txt", {t/.tt2, y1}//Transpose, "Table"];
	g = Show[{g1, g2}];
	Export["b1.eps", g, "EPS"];

	Export["dp.txt", {t/.tt2, stand, d2, d3}//Transpose, "Table"];

]

(*------------------------------------------------------------*)
(* 准备微分方程预测数据 *)
aaaa[s_, scale_, rate_, future_, x0_, dx_] := Module[{},
	n = Length[s];   		             (* 样本数量 *)
	k = Floor[n/2*rate];                 (* 滤波保留的数据长度 *)

	(* 通过傅立叶变换, 进行滤波, 并增加数据采样点, 细节放大 scale 倍 *)
	q = Fourier[s];
	q = Drop[q, {k+1, n-k}];									(* 删除中间部分的数据 *)
	q = Insert[q, Table[0, {a, k+1, n-k}], k+1] // Flatten;		(* 再添加相应数量的 0 *)
	q = Insert[q, Table[0, {a, 1, n*(scale-1)}], n/2+1] // Flatten;	(* 将数据扩展到原来的 scale 倍 *)
	q = InverseFourier[q] // Re;
	q = q * Sqrt[scale];

(*	g1 = ListPlot[q, PlotJoined->True]; *)

	(* 求一阶, 二阶导数 *)
	pn1 = (Range[2, n-1]-1) * scale + 1 // N;
	pn0 = pn1-1;
	pn2 = pn1+1;

	Print[pn0[[1]], pn0[[2]], pn1[[1]]];

	x = (Range[2, n-1]-1) * dx + x0 // N;
	h = dx / scale // N;

	t0 = q[[pn0]];
	t1 = q[[pn1]];
	t2 = q[[pn2]];

	y0 = t0;                                      (* 函数值本身 *)
	y1 = (t2-t0) / (h*2);                   (* 求一阶导数 *)
(*	y1 = (t1-t0) / (h);                   (* 求一阶导数 *) *) 
	y2 = (t0-2*t1+t2) / (h*h);        (* 求二阶导数 *)

	y1 = Drop[y1, 4];
	y1 = Drop[y1, -14];
	g2 = ListPlot[y1, PlotRange->{-0.85, 0.85}];
]
