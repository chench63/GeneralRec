<<Graphics`MultipleListPlot` 


showSampleOde[ode_] := Module[{t, g1, g2},
	t = -b-c;
	t = t/.{a->x, b->y[x], c->y'[x]};
	Print[y''[x]==t];
	t = NDSolve[{y''[x]==t, y[0]==0, y'[0]==2}, y, {x, 2, 8}];
	g1 = Plot[y[x]/.t, {x, 2, 8}];

	t = Simplify[ode];
	t = t/.{a->x, b->y[x], c->y'[x]};
	Print[y''[x]==t];
	t = NDSolve[{y''[x]==t, y[0]==0, y'[0]==2}, y, {x, 2, 8}];
	g2 = Plot[y[x]/.t, {x, 2, 8}];

	Show[{g1, g2}];

	Null
]

(*------------------------------------------------------------*)

(* 显示太阳黑子 微分方程预测结果 *)
showSunspotOde[ode_] := Module[{time, data},
	time = Import["sunspot.txt", "List"];
	time = Take[time, -140];
	
	data = Import["sunspot_ode.txt", "Table"];
	data = Take[data, -140];
	showOde[ode, time, data];

	Null
]

(* 显示微分方程预测结果 *)
showOde[ode_, time_, data_] := Module[{n, t, i, ans, list, dx, x0, y0, y1},
	t = Simplify[ode];
	t = t/.{a->x, b->y[x], c->y'[x]};
	Print[y''[x]==t];

	xx = Range[1989-Length[data]+1, 1989];

	Print["xx: ", Take[xx, -2]];
	Print["data: ", Take[data, -1]];

	n = Length[data];
	dx = data[[2,1]] - data[[1,1]];

	Print["dx: ", dx];

	list = {};
	For[i=1, i<=n, i++,
		x0 = data[[i, 1]];
		y0 = data[[i, 2]];
		y1 = data[[i, 3]];
		ans = NDSolve[{y''[x]==t, y[x0]==y0, y'[x0]==y1}, y, {x, x0, x0+dx}];
		ans = (y /. ans[[1]])[x0+dx] // N;
		list = Append[list, ans];
	];

	MultipleListPlot[time, list, PlotJoined->True];

	Export["ode.txt", {xx, time, list} // Transpose, "Table"];
]

(*======================================================================================*)

(* 显示例子直接预测结果 *)
showSampleDirect[expression_] := Module[{data},
	data = Import["sample_direct.txt", "Table"];
	data = Take[data, {21, 80}];
	
	showDirect[data, expression]
]

(*------------------------------------------------------------*)

(* 显示太阳黑子直接预测结果 *)
showSunspotDirect[expression_] := Module[{data},
	data = Import["sunspot.txt", "List"];
	data = Partition[data, 13, 1];
	data = Take[data, -140];

	showDirect[data, expression]
]

(*------------------------------------------------------------*)

(* 显示例子直接预测结果 *)
showDirect[data_, expression_] := Module[{temp, formula, xx, g1},
	formula = Simplify[expression];
	Print[formula];

	xx = Range[1989-Length[data]+1, 1989];

	temp = Transpose[data];
	dd1 = temp[[13]];
(*	dd1 = {xx, dd1} // Transpose; *)

	rules = {
		Map[a->#1 &, temp[[1]]],
		Map[b->#1 &, temp[[2]]],
		Map[c->#1 &, temp[[3]]],
		Map[d->#1 &, temp[[4]]],
		Map[e->#1 &, temp[[5]]],
		Map[f->#1 &, temp[[6]]],
		Map[g->#1 &, temp[[7]]],
		Map[h->#1 &, temp[[8]]],
		Map[i->#1 &, temp[[9]]],
		Map[j->#1 &, temp[[10]]],
		Map[k->#1 &, temp[[11]]],
		Map[l->#1 &, temp[[12]]]
	};
	rules = Transpose[rules];

	dd2 = formula /. rules;
(*	dd2 = {xx, dd2} // Transpose; *)

	g1 = MultipleListPlot[dd2, dd1, PlotJoined->True];

	Export["direct.txt", {xx, dd1, dd2} // Transpose, "Table"];
(*	Export["direct.eps", g1, "EPS"]; *)
]

(*======================================================================================*)

(* 准备直接预测例子数据 *)
prepareSampleDirect[] := Module[{formula, data},
	formula = (4*Sin[(Sqrt[3]*x)/2])/(Sqrt[3]*E^(x/2));
	data = Table[formula, {x, 0, 10, 0.1}];
	data = prepareDirect[data, 0.4, 10, 1];
	Export["sample_direct.txt", data, "Table"]
]

(*------------------------------------------------------------*)

(* 准备直接预测例子数据 *)
prepareSunspotDirect[] := Module[{data},
	data = Import["sunspot.txt", "List"];
	data = prepareDirect[data, 0.8, 12, 3];

	Export["sunspot_direct.txt", data, "Table"]
]

(*------------------------------------------------------------*)

(*
	准备直接预测的数据
	s        待处理的数据序列
	rate     滤波保留的频率比例
	x0       自变量的起始值
	dx       自变量的间隔
*)
prepareDirect[s_, rate_, history_, future_] := Module[{n, k, t, a},
	n = Length[s];   		             (* 样本数量 *)
	k = Ceiling[n/2*rate];               (* 滤波保留的数据长度 *)

	(* 通过傅立叶变换, 进行滤波 *)
	t = s;
	t = Fourier[t];
	t = Drop[t, {k+1, n-k}];									(* 删除中间部分的数据 *)
	t = Insert[t, Table[0, {a, k+1, n-k}], k+1] // Flatten;		(* 再添加相应数量的0 *)
	t = InverseFourier[t] // Re;

	Partition[t, history+future, 1]
]

(*======================================================================================*)

(* 准备太阳黑子微分方程预测数据 *)
prepareSunspotOde[] := Module[{data},
	data = Import["sunspot.txt", "List"];
	data = prepareOde[data, 10, 0.8, 1, 1700.0, 1.0];

	Export["sunspot_ode.txt", data, "Table"]
]

(*------------------------------------------------------------*)
(* 准备微分方程预测数据 *)
prepareOde[s_, scale_, rate_, future_, x0_, dx_] := Module[{n, k, t, pn0, pn1, pn2, x, h, y0, y1, y2, data, i},
	n = Length[s];   		             (* 样本数量 *)
	k = Floor[n/2*rate];                 (* 滤波保留的数据长度 *)

	(* 通过傅立叶变换, 进行滤波, 并增加数据采样点, 细节放大 scale 倍 *)
	t = Fourier[s];
	t = Drop[t, {k+1, n-k}];									(* 删除中间部分的数据 *)
	t = Insert[t, Table[0, {a, k+1, n-k}], k+1] // Flatten;		(* 再添加相应数量的 0 *)
	t = Insert[t, Table[0, {a, 1, n*(scale-1)}], n/2+1] // Flatten;	(* 将数据扩展到原来的 scale 倍 *)
	t = InverseFourier[t] // Re;
	t = t * Sqrt[scale];

	(* 求一阶, 二阶导数 *)
	pn1 = (Range[2, n]-1) * scale + 1 // N;
	pn0 = pn1-1;
	pn2 = pn1+1;

	x = (Range[2, n]-1) * dx + x0 // N;
	h = dx / scale // N;

	y0 = t[[pn1]];                                      (* 函数值本身 *)
	y1 = (t[[pn2]]-t[[pn0]]) / (h*2);                   (* 求一阶导数 *)
	y2 = (t[[pn0]]-2*t[[pn1]]+t[[pn2]]) / (h*h);        (* 求二阶导数 *)

	z = y0;

	(* 准备输出数据 *)
	data = {x, y0, y1};
	For[i=1, i<=future, i++,
		z = Rest[z];
		z = Append[z, 0];
		data = Append[data, z];
	];

	data = Transpose[data];
	data = Drop[data, -future];

	data
]
