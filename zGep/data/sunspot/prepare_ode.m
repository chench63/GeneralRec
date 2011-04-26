function data = prepare_ode(s, x0, dx, rate, scale, future)
% prepare_ode(s, x0, dx, rate, scale, future)
% s        待处理的数据序列  (要求为列向量)
% rate     滤波保留的频率比例
% scale    细节放大的倍数
% x0       自变量的起始值
% dx       自变量的间隔

%s = load('sunspot.txt');
%x0 = 0.0;
%dx = 0.1;
%rate = 0.5;
%scale = 100;
%future = 5;

N = size(s, 1);   		             %样本数量
k = floor(N/2*rate);                 %滤波保留的数据长度
rank = 2;                            %微分方程的阶

M = N*scale;

%通过傅立叶变换, 进行滤波, 并增加数据采样点, 细节放大scale倍
Y = fft(s);
z = ifft(Y);
z = real(z);

T = zeros(M, 1);
T(1:k) = Y(1:k);
T(M-k+1:M) = Y(N-k+1:N);
t = ifft(T)*scale;
t = real(t);

%求一阶, 二阶导数
pn1 = ([2:N]-1)*scale+1;
pn0 = pn1-1;
pn2 = pn1+1;

x = ([2:N]-1)*dx+x0;
x = x';
h = dx/scale;
y0 = t(pn1);                                     %函数值本身
y1 = (t(pn2)-t(pn0)) / (h*2);                   %求一阶导数
y2 = (t(pn0)-2*t(pn1)+t(pn2)) / (h*h);         %求二阶导数

%准备输出数据
%data = [x, y0, y1, y2];
data = [x, y0, y1];
for i=[1:future]
   y0(1, :) = [];
   y0 = [y0; 0];
	data = [data, y0];
end

data(N-1-future+1:N-1, :) = [];
