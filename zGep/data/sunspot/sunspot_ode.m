% 为使用直接预测法进行太阳黑子的预测准备数据

clear;

s = load('sunspot.txt');
x0 = 0.0;
dx = 0.1;
rate = 0.5;
scale = 100;
future = 5;

data = prepare_ode(s, x0, dx, rate, scale, future);

save('sunspot_ode.txt', 'data', '-ascii')
