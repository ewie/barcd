MAIN = document
UML = uml

.PHONY: all $(MAIN) $(UML) clean cleanaux

all: $(UML) $(MAIN)

$(MAIN): $(MAIN).tex
	pdflatex $<

$(UML):
	cd ./img/uml; \
	rm -f `ls | grep -P "\.\d+"`; \
	mpost $(UML).mp; \
	for file in `ls | grep -P "\.\d+"`; do \
		sed -i -E 's/(%%CreationDate:).*/\1 [STRIPPED]/' $$file; \
	done; \
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

