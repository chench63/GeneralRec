%prologues:=1;

input boxes

beginfig(1)
defaultfont:="cmtt10";

  circleit.node0("+");
  circleit.node1("+");
  circleit.node2("-");
  circleit.node3("c");
  circleit.node4("-");
  circleit.node5("c");
  circleit.node6("7");
  circleit.node7("-");
  circleit.node8("*");
  circleit.node9("/");
  circleit.node10("+");
  circleit.node11("8");
  circleit.node12("c");
  circleit.node13("0");
  circleit.node14("S");
  circleit.node15("c");

  node0.n - node0.s = (0, 12);
  node0.e - node0.w = (12, 0);
  node1.n - node1.s = (0, 12);
  node1.e - node1.w = (12, 0);
  node2.n - node2.s = (0, 12);
  node2.e - node2.w = (12, 0);
  node3.n - node3.s = (0, 12);
  node3.e - node3.w = (12, 0);
  node4.n - node4.s = (0, 12);
  node4.e - node4.w = (12, 0);
  node5.n - node5.s = (0, 12);
  node5.e - node5.w = (12, 0);
  node6.n - node6.s = (0, 12);
  node6.e - node6.w = (12, 0);
  node7.n - node7.s = (0, 12);
  node7.e - node7.w = (12, 0);
  node8.n - node8.s = (0, 12);
  node8.e - node8.w = (12, 0);
  node9.n - node9.s = (0, 12);
  node9.e - node9.w = (12, 0);
  node10.n - node10.s = (0, 12);
  node10.e - node10.w = (12, 0);
  node11.n - node11.s = (0, 12);
  node11.e - node11.w = (12, 0);
  node12.n - node12.s = (0, 12);
  node12.e - node12.w = (12, 0);
  node13.n - node13.s = (0, 12);
  node13.e - node13.w = (12, 0);
  node14.n - node14.s = (0, 12);
  node14.e - node14.w = (12, 0);
  node15.n - node15.s = (0, 12);
  node15.e - node15.w = (12, 0);

  node1.c = node0.c + (-54.6875,-25.0);
  node2.c = node1.c + (-18.75,-25.0);
  node3.c = node2.c + (0.0,-25.0);
  node4.c = node1.c + (18.75,-25.0);
  node5.c = node4.c + (-12.5,-25.0);
  node6.c = node4.c + (12.5,-25.0);
  node7.c = node0.c + (54.6875,-25.0);
  node8.c = node7.c + (0.0,-25.0);
  node9.c = node8.c + (-21.875,-25.0);
  node10.c = node9.c + (-18.75,-25.0);
  node11.c = node10.c + (-12.5,-25.0);
  node12.c = node10.c + (12.5,-25.0);
  node13.c = node9.c + (18.75,-25.0);
  node14.c = node8.c + (21.875,-25.0);
  node15.c = node14.c + (0.0,-25.0);

  drawboxed(node0);
  drawboxed(node1);
  drawboxed(node2);
  drawboxed(node3);
  drawboxed(node4);
  drawboxed(node5);
  drawboxed(node6);
  drawboxed(node7);
  drawboxed(node8);
  drawboxed(node9);
  drawboxed(node10);
  drawboxed(node11);
  drawboxed(node12);
  drawboxed(node13);
  drawboxed(node14);
  drawboxed(node15);

  draw node0.s -- node1.n;
  draw node1.s -- node2.n;
  draw node2.s -- node3.n;
  draw node1.s -- node4.n;
  draw node4.s -- node5.n;
  draw node4.s -- node6.n;
  draw node0.s -- node7.n;
  draw node7.s -- node8.n;
  draw node8.s -- node9.n;
  draw node9.s -- node10.n;
  draw node10.s -- node11.n;
  draw node10.s -- node12.n;
  draw node9.s -- node13.n;
  draw node8.s -- node14.n;
  draw node14.s -- node15.n;

endfig;

end
