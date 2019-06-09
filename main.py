"""
# Author:   Hop N Pham
# Version:     6/10/2019
# Assigment:   Trigram	model	of	English
"""

import random
STORY_LEN = 10 # len of the story to generates.

class Tri_Gram:
    
    # Initializes
    def __init__(self, count, theTri_Sequence):
        self.structor = []
        self.appeared = 1
        self.count = count
        self.element = theTri_Sequence[0]
        self.__insert(len(theTri_Sequence) > 1, theTri_Sequence)

    def getWords(self):
        story = []
        position = random.randint(0, self.appeared - 1)
        for successor in self.structor:
            position -= successor.appeared
            if position < 0:
                story.append(successor.element)
                story += (successor.getWords())
                break
        return story
		
    def __insert(self,len,theTri_Sequence):
        if (len) : self.structor.append(Tri_Gram(self.count + 1, theTri_Sequence[1:]))	
		
    def gramUpdate(self, theTri_Sequence):
        self.appeared += 1
        exists = False
        for successor in self.structor:
            if theTri_Sequence[0] == successor.element:
                exists = True
                successor.gramUpdate(theTri_Sequence[1:])
                break
        if not exists and len(theTri_Sequence) > 0:
            self.structor.append(Tri_Gram(self.count + 1, theTri_Sequence))
    

def loadWords(stories):
    words = []
    for story in stories:
        with open(story, 'r') as lines:
            for line in lines:
                words.extend(line.lower().split())
    return words   
	
def generateStory(theStructor):
    story = []
    story.append(random.choice(theStructor).element) # Randomly select first word.
    # Generate the story base on the requirement length.
    while len(story) < STORY_LEN:
        for word in theStructor:
            if word.element == story[-1]:
                story += word.getWords()
                break
				
    for i in range (STORY_LEN, len(story)):
        story[i] = '';
    with open('output.txt','w') as f:
        f.write( ' '.join( story ) )
    print 'Finished, please check the output file output.txt'
	
def updateModel(elem, target):	
    if (elem.element == target[0]):
        elem.gramUpdate(target[1:])
        return True
    return False
	
if __name__ == '__main__':
    stories = ['alice-27.txt']#,'doyle-27.txt','doyle-case-27.txt','london-call-27.txt','melville-billy-27.txt','twain-adventures-27.txt']
    tri_Sequence = []
    words = loadWords(stories)
    structor = [] 

    print 'Loading stories...'
    for word in words:
        tri_Sequence.append(word.lower())
        if len(tri_Sequence) == 3: #find new sequence
            if not any(updateModel(elem,tri_Sequence) for elem in structor): structor.append(Tri_Gram(0, tri_Sequence))
            tri_Sequence.remove(tri_Sequence[0])
    print 'Stories loaded. The program is generating story...'   
    generateStory(structor)