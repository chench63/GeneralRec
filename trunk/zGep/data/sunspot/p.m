(* ׼��̫������΢�ַ���Ԥ������ *)
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
(* ׼��΢�ַ���Ԥ������ *)
aaaa[s_, scale_, rate_, future_, x0_, dx_] := Module[{},
	n = Length[s];   		             (* �������� *)
	k = Floor[n/2*rate];                 (* �˲����������ݳ��� *)

	(* ͨ������Ҷ�任, �����˲�, ���������ݲ�����, ϸ�ڷŴ� scale �� *)
	q = Fourier[s];
	q = Drop[q, {k+1, n-k}];									(* ɾ���м䲿�ֵ����� *)
	q = Insert[q, Table[0, {a, k+1, n-k}], k+1] // Flatten;		(* �������Ӧ������ 0 *)
	q = Insert[q, Table[0, {a, 1, n*(scale-1)}], n/2+1] // Flatten;	(* ��������չ��ԭ���� scale �� *)
	q = InverseFourier[q] // Re;
	q = q * Sqrt[scale];

(*	g1 = ListPlot[q, PlotJoined->True]; *)

	(* ��һ��, ���׵��� *)
	pn1 = (Range[2, n-1]-1) * scale + 1 // N;
	pn0 = pn1-1;
	pn2 = pn1+1;

	Print[pn0[[1]], pn0[[2]], pn1[[1]]];

	x = (Range[2, n-1]-1) * dx + x0 // N;
	h = dx / scale // N;

	t0 = q[[pn0]];
	t1 = q[[pn1]];
	t2 = q[[pn2]];

	y0 = t0;                                      (* ����ֵ���� *)
	y1 = (t2-t0) / (h*2);                   (* ��һ�׵��� *)
(*	y1 = (t1-t0) / (h);                   (* ��һ�׵��� *) *) 
	y2 = (t0-2*t1+t2) / (h*h);        (* ����׵��� *)

	y1 = Drop[y1, 4];
	y1 = Drop[y1, -14];
	g2 = ListPlot[y1, PlotRange->{-0.85, 0.85}];
]
