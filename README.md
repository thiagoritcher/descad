# DESCAD
CAD descritivo

Desenho a partir de informações em arquivo CSV

## Comando
descad --input,-i <input_file> --output,-o <dxf_file>

##Exemplo de entrada
`Modules
ID	W	H	Commands
m1	80	15	pl	-40	-7.5	40	-7.5	25	7.5
m2	5	5	pl	-2.5	-2.5	2.5	-2.5	0	5

Design
ID	W	H	Loc	Module	dx	dy
1	150	150	0	R	0	0
2	80	150	O1	R
3	20	150	O2	R
4	80	150	L1	R
5	20	150	L4	R
6	150	80	S1	R
7	150	20	S6	R
8	150	80	N1	R
9	153	153	N8	R
10	20	153	O9	R
11	15	153	O10	R
12	20	153	L9	R
13	15	153	L12	R
14	153	20	N9	R
15	153	15	N14	R
16	0	0	N2	m1
17	0	0	S2	m1
18	0	0	N4	m1
19	0	0	S4	m1
20	35	20	O14	R
21	35	20	L14	R
22	0	0	S21	m2	2.5	0
23	0	0	S20	m2	-2.5	0`