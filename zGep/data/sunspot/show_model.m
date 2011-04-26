% 显示模型数据和测试数据

clear;

N = 0;

test = load('test.txt');
model = load('model.txt');
N = size(test, 1);

x = [1:N]';

plot(x, test(:, 1), 'b.-', x, model, 'r-o');