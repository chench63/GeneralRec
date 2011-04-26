% 为使用微分方程预测法进行演示的预测准备数据
%DSolve[{y''[x] == -y'[x] - y[x], y[0] == 0, y'[0] == 2}, y[x], x]
%y[x] = (4*Sin[(Sqrt[3]*x)/2])/(Sqrt[3]*E^(x/2))

clear;

x = [0:0.1:10]';
y = exp(-0.5*x) .* (2.3094 * sin(0.8660 * x));
x0 = 0.0;
dx = 0.1;
rate = 0.3;
scale = 100;
future = 5;

data = prepare_ode(y, x0, dx, rate, scale, future);

save('sample_ode.txt', 'data', '-ascii')

y1 = (-0.6667*(-3 .* cos(0.8660*x) +  1.7321* sin(0.8660*x)))./exp(0.5*x);