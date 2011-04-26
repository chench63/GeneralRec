function data = prepare_ode(s, x0, dx, rate, scale, future)
% prepare_ode(s, x0, dx, rate, scale, future)
% s        ���������������  (Ҫ��Ϊ������)
% rate     �˲�������Ƶ�ʱ���
% scale    ϸ�ڷŴ�ı���
% x0       �Ա�������ʼֵ
% dx       �Ա����ļ��

%s = load('sunspot.txt');
%x0 = 0.0;
%dx = 0.1;
%rate = 0.5;
%scale = 100;
%future = 5;

N = size(s, 1);   		             %��������
k = floor(N/2*rate);                 %�˲����������ݳ���
rank = 2;                            %΢�ַ��̵Ľ�

M = N*scale;

%ͨ������Ҷ�任, �����˲�, ���������ݲ�����, ϸ�ڷŴ�scale��
Y = fft(s);
z = ifft(Y);
z = real(z);

T = zeros(M, 1);
T(1:k) = Y(1:k);
T(M-k+1:M) = Y(N-k+1:N);
t = ifft(T)*scale;
t = real(t);

%��һ��, ���׵���
pn1 = ([2:N]-1)*scale+1;
pn0 = pn1-1;
pn2 = pn1+1;

x = ([2:N]-1)*dx+x0;
x = x';
h = dx/scale;
y0 = t(pn1);                                     %����ֵ����
y1 = (t(pn2)-t(pn0)) / (h*2);                   %��һ�׵���
y2 = (t(pn0)-2*t(pn1)+t(pn2)) / (h*h);         %����׵���

%׼���������
%data = [x, y0, y1, y2];
data = [x, y0, y1];
for i=[1:future]
   y0(1, :) = [];
   y0 = [y0; 0];
	data = [data, y0];
end

data(N-1-future+1:N-1, :) = [];
