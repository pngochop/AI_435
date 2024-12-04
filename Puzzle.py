"""
# File:     Puzzle.py
# Author:   Hop Pham
# Version:     4/28/2019
# Purpose:  Create a set of	search algorithms that find	goalStates to the 15-puzzle
"""

import sys
from copy import deepcopy
			
class DataUtility:    
    def __init__(self,initialState):
        self.queue = []
        self.visisted = []
        self.initialState = initialState
        self.queue.append(initialState)
        self.stack = self.queue
        self.priority = None
        self.numCreated = 0
        self.numExpanded = 0
        self.maxFringe = 0
        self.depth = -1
    def setPriority(self,heuristic):
        self.queue = PriorityQ()
        if heuristic == 'h1':
            self.priority = self.initialState.countMisplaced()
        elif heuristic == 'h2':
            self.priority = self.initialState.sumDistFromGoal()
    def __repr__(self): 
        if self.depth == -1:
            self.numCreated = self.numExpanded = self.maxFringe = 0
        return '%d, %d, %d, %d' % (self.depth, self.numCreated, self.numExpanded, self.maxFringe)

# PriorityQ for GBFS and AStar
class PriorityQ:
    def __init__(self): 
        self.nodes = []
        self.weight = [] 	
		
    def __len__(self):
        return len(self.nodes)
		
    def delete(self):
        if self.nodes:
            self.weight.pop(0)
            return self.nodes.pop(0)
    def peak(self):
        if self.nodes:
            return self.nodes.peak()
    def insert(self, weight, node):
        for i in range(len(self.nodes)):
            if weight < self.weight[i]:
                self.weight.insert(i, weight)
                self.nodes.insert(i, node)
                return
        self.weight.append(weight)
        self.nodes.append(node)    
    
    def __repr__(self):
        result = '\n'
        for i in range(len(self.nodes)):
            result += str(self.weight[i]) + '\n' + str(self.nodes[i])
        return result

#Puzzle class
class Puzzle:    
    arr = []
    blankPosition = None
    depth = None
    goalState = []
    def __init__(self, initialState):
        self.arr = list(initialState)
        self.depth = 0
        self.blankPosition = self.arr.index(' ')
        self.goalState = ['1', '2', '3', '4', 
                     '5', '6', '7', '8', 
                     '9', 'A', 'B', 'C', 
                     'D', 'E', 'F', ' ']
        self.goalState2 = ['1', '2', '3', '4', 
                     '5', '6', '7', '8', 
                     '9', 'A', 'B', 'C', 
                     'D', 'F', 'E', ' ']
        if set(self.arr) != set(self.goalState):
            print 'Invalid puzzle!'
            sys.exit(-1)
    def __eq__(self, other):
        return self.arr == other.arr
    
    def __repr__(self):
        result = ''
        for i in range(4):
            result += str(self.arr[(i*4):((i+1)*4)])
            result += "\n"
        return result
        
    def isReachGoal(self):
        return self.arr[0:12] == self.goalState[0:12] and self.arr[15] == self.goalState[15] or self.arr[0:12] == self.goalState2[0:12] and self.arr[15] == self.goalState2[15]
    
    def getSuccessor(self):
        successor = []
        for direction in ['Right', 'Down', 'Left', 'Up']:
            succe = None
            if direction == 'Left' and self.blankPosition % 4 > 0:
                succe = deepcopy(self)
                blank = self.blankPosition - 1
				
            if direction == 'Right' and self.blankPosition % 4 < 3:
                succe = deepcopy(self)
                blank = self.blankPosition + 1
                    
            if direction == 'Up' and self.blankPosition > 3:
                succe = deepcopy(self)
                blank = self.blankPosition - 4
				
            if direction == 'Down' and self.blankPosition < 12:
                succe = deepcopy(self)
                blank = self.blankPosition + 4
            
            if succe:
                succe.depth += 1        
                succe.arr[succe.blankPosition] = succe.arr[blank]
                succe.arr[blank] = ' '
                succe.blankPosition = blank
                successor.append(succe)
        return successor

    def countMisplaced(self):
        # h for number of misplaced tiles
        n = 0
        for index, tile in enumerate(self.arr):
            if tile != self.goalState[index] or tile != self.goalState2[index]: n += 1
        return n
    
    def sumDistFromGoal(self):
        # h for sum of distance to goalState
        mSum = 0
        for tileIndex, tile in enumerate(self.arr):
            solIndex = self.goalState.index(tile);
            vDistance = abs(solIndex/4 - tileIndex/4)
            hDistance = abs(solIndex%4 - tileIndex%4)
            mSum += vDistance + hDistance
        return mSum
		
def searchMethodBFS(initialState):
    #used queue to expanded nodes
    data = DataUtility(initialState)
    while data.queue:
        current = data.queue.pop(0)
        if current.isReachGoal():
            data.depth = current.depth
            return data
        data.numExpanded += 1
        data.visisted.append(current)
        successor = current.getSuccessor()
        for succe in successor:
            if succe not in data.visisted:
                data.numCreated += 1
                data.queue.append(succe)
                if len(data.queue) > data.maxFringe:
                    data.maxFringe = len(data.queue)
    return data
    
def searchMethodDLS(initialState, maxDepth):
    data = DataUtility(initialState)
    while data.stack:
        current = data.stack.pop()
        if current.isReachGoal():
            data.depth = current.depth
            return data
        data.numExpanded += 1
        data.visisted.append(current)
        successor = current.getSuccessor()
        for succe in successor:
            if succe not in data.visisted and succe.depth <= int(maxDepth):
                data.numCreated += 1
                data.stack.append(succe)
                if len(data.stack) > data.maxFringe:
                    data.maxFringe = len(data.stack)
    return data
                    
def searchMethodDFS(initialState):
    #used stack to expanded nodes
    data = DataUtility(initialState)
    while data.stack:
        current = data.stack.pop()
        data.stack.append(current)
        if current.isReachGoal():
            data.depth = current.depth
            return data
        data.numExpanded += 1
        data.visisted.append(current)
        successor = current.getSuccessor()
        for succe in successor:
            if succe not in data.visisted:
                data.numCreated += 1
                data.stack.append(succe)
                if len(data.stack) > data.maxFringe:
                    data.maxFringe = len(data.stack)
        #if data.numCreated > 3000:
        #    print "The numCreated is exceed 3000"
        #    sys.exit(-1)
    return data #default stats
	
def searchMethodID(initialState):    
    data = DataUtility(initialState)
    maxDepth = 0
    while maxDepth < 50: # Depth limit to reach the goal state
        while data.stack:
            current = data.stack.pop()
            if current.isReachGoal():
                data.depth = current.depth
                return data
            data.numExpanded += 1
            data.visisted.append(current)
            successor = current.getSuccessor()
            for succe in successor:
                if succe not in data.visisted and succe.depth <= maxDepth:
                    data.numCreated += 1
                    data.stack.append(succe)
                    if len(data.stack) > data.maxFringe:
                        data.maxFringe = len(data.stack)
        maxDepth += 1
        data.stack.append(data.initialState)
        data.visisted = []
    return data

def searchMethodGBFS(initialState, heuristic):
    data = DataUtility(initialState)
    data = DataUtility(initialState)
    data.setPriority(heuristic)
    data.queue.insert(data.priority, data.initialState)

    while data.queue:
        current = data.queue.delete()
        if current.isReachGoal():
            data.depth = current.depth
            return data
        data.numExpanded += 1
        data.visisted.append(current)
        successor = current.getSuccessor()
        for succe in successor:
            if succe not in data.visisted:
                data.numCreated += 1
                if heuristic == 'h1':
                    data.priority = succe.countMisplaced()
                elif heuristic == 'h2':
                    data.priority = succe.sumDistFromGoal()
                data.queue.insert(data.priority, succe)
                if len(data.queue) > data.maxFringe:
                    data.maxFringe = len(data.queue)
    return data

def searchMethodAStar(initialState, heuristic):
    data = DataUtility(initialState)
    data.setPriority(heuristic)
    data.queue.insert(data.priority + data.initialState.depth, data.initialState)
	
    while data.queue:
        current = data.queue.delete()
        if current.isReachGoal():
            data.depth = current.depth
            return data
        data.visisted.append(current)
        data.numExpanded += 1
        successor = current.getSuccessor()
        for succe in successor:
            if heuristic == 'h1':
                data.priority = succe.countMisplaced()
            elif heuristic == 'h2':
                data.priority = succe.sumDistFromGoal()
            if succe not in data.visisted:
                data.numCreated += 1
                data.queue.insert(data.priority + succe.depth, succe)
                if len(data.queue) > data.maxFringe:
                    data.maxFringe = len(data.queue)
            elif data.visisted[data.visisted.index(succe)].depth > succe.depth:
                data.visisted[data.visisted.index(succe)].depth = succe.depth
                data.queue.insert(data.priority + succe.depth, succe)
                
    return data 
	
def unity(args):
    initialState = ''
    searchMethod = ''
    result = ''
    options = ''
    
    # checks valid for input tokens
    if len(args) in range(3, 5):
        initialState = Puzzle(args[1])        		
        searchMethod = args[2]
        if len(args) > 3:
            options = args[3]            
    else:
        print "Input is not valid!"
        sys.exit(-1)		

    if searchMethod == 'BFS':
        result = searchMethodBFS(initialState)        
    elif searchMethod == 'DFS':
        result = searchMethodDFS(initialState)        
    elif searchMethod == 'DLS':
        if options == '' or not int(options):
            print 'Please input an integer depth for DLS!'
            sys.exit(-1)
        result = searchMethodDLS(initialState, options)        
    elif searchMethod == 'ID':
        result = searchMethodID(initialState)    
    elif searchMethod == 'GBFS' and (options == 'h1' or options == 'h2'):
        result = searchMethodGBFS(initialState,options)        
    elif searchMethod == 'AStar' and (options == 'h1' or options == 'h2'):
        result = searchMethodAStar(initialState,options)        
    else:
        print 'Invalid search method'
        sys.exit(-1)
		
    return result

if __name__ == '__main__':
    print unity(sys.argv)    