MAIN = document
UML = uml

pdf: $(MAIN).pdf

$(MAIN).pdf: $(MAIN).tex uml
	pdflatex $(MAIN).tex

uml:
	cd ./img/uml; \
	mpost $(UML).mp; \
	cd -

cleanaux:
	rm -f $(MAIN).aux
	rm -f $(MAIN).bbl
	rm -f $(MAIN).blg
	rm -f $(MAIN).lof
	rm -f $(MAIN).log
	rm -f $(MAIN).lot
	rm -f $(MAIN).out
	rm -f $(MAIN).tdo
	rm -f $(MAIN).toc

clean: cleanaux
	rm -f $(MAIN).pdf
	cd ./img/uml; rm -f $(UML).log `ls | grep -P "\.\d+"`

