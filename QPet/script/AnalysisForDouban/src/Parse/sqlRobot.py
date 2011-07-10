'''
Created on 2011-7-10

@author: Administrator
'''

class SqlRobot(object):
    '''
    Sql Robot Generate sql files
    '''
    def __init__(self):
        self.para = []
        self.type = []
        self.sql = ''
        self.__rIndex = 0
    
    def createRobot(self,sql):
        self.sql= sql
    
    def setParameter(self, type, token):
        self.para.append(token)
        self.type.append(type)
    
    def __replace(self, sqlCopy, sour, index):
        rindex = sqlCopy.index(sour,self.__rIndex)
        sql_front =  sqlCopy[:rindex]
        sql_end =  sqlCopy[rindex+1:]
        
        
        if not cmp(self.type[index],'INT'):
            self.__rIndex = rindex+ len(self.para[index])
            return sql_front+self.para[index]+sql_end
        elif not cmp(self.type[index],'STR'):
            token = '\''+self.para[index]+'\''
            self.__rIndex = rindex+ len(token)
            return sql_front+token+sql_end
        
    def generateSQL(self):
        sqlCopy= self.sql
        for i in range(0,self.para.__len__()):
            try:               
                sqlCopy = self.__replace( sqlCopy, '?', i ) 
            except Exception, e:
                print 'Check Your Sql Format..',e
                self.para = []
                return None
        self.para= []
        self.__rIndex = 0
        return sqlCopy
            
            
            
            
            
            
            
