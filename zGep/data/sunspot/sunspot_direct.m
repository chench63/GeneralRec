% 为使用直接预测法进行太阳黑子的预测准备数据

clear;

s = load('sunspot.txt');

data = prepare_direct(s);