
all:
	latex teamfound.tex
	dvips teamfound.dvi > teamfound.ps
#makeindex teamfound.idx -o teamfound.ind
	ps2pdf teamfound.ps

slides:
	latex slides.tex
	dvi2ps -o landscape slides.dvi > slides.ps
	ps2pdf slides.ps
