import nltk    #importing nltk package
for i in range (0,768):
	out_file = open("stem/tokens"+str(i)+".txt", "w")
	file_content = open("tok/split"+str(i)+".txt").read()
	words = nltk.word_tokenize(file_content)              #tokenizing into the out_file
	for word in words:
		   out_file.write(word)
		   out_file.write("\n")
