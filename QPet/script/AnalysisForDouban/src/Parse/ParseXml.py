'''
Created on 2011-5-26

@author: ppiachen@gmail.com
'''

from xml.dom import minidom
from ctypes import Structure

import sqlRobot

fileXml = "./webLib/index_1.xml"

class ItemDB(Structure):
    _fields_ =[]
    
class ItemDB_List():
    tagSet=[]
    def __init__(self):
        self.imgUrl=''
        self.title=''
        self.url=''
        
    @classmethod    
    def addTag(cls,tag):
        cls.tagSet.append(tag)
        
        
    def __getURL(self,strCache,areaBeg,areaEnd,tagBeg,tagEnd):
        Beg=strCache.find(areaBeg)
        End=strCache.find(areaEnd,Beg)+1
        imgBuffer = strCache[Beg:End]
        imgBeg=imgBuffer.find(tagBeg)
        imgEnd=imgBuffer.find(tagEnd,imgBeg)
        return imgBuffer[imgBeg:imgEnd]
        
    def __setEle(self,eachEle):
        for each in eachEle.childNodes:
            if not cmp(eachEle.nodeName,'title'):
                self.title= each.data
            elif not cmp(eachEle.nodeName,'description'):
                self.url= self.__getURL(each.data, '(http:', ')', 'http:', ')')
            elif not cmp(eachEle.nodeName,'content:encoded'):
                self.imgUrl= self.__getURL(each.data,'<img', '>', 'http:', '"')
                
    
    def setItem(self,eachItem):
        for eachEle in eachItem.childNodes:
            if eachEle.nodeName in self.tagSet:
                self.__setEle(eachEle)
       
#def createSQL(item, id):
#    sql_front = "insert into ItemMatrix (imgUrl, context, webUrl, itemId) "
#    sql_value = "values( \'"+item.imgUrl +"\' , \'"+item.title+"\' , \'"+item.url+"\' , %d) "  %id
#    sql_back =  ";\n"
#    return sql_front + sql_value + sql_back
                
            
def CreatePetLib():
    fo = open("./qpetLib/qpetData.sql","w")
    robot = sqlRobot.SqlRobot()
    sql = 'insert into PetLib (petId, srcUrl, petExt, petExt1) values(?,?,?,?);\n'
    
    
    
    
    robot.createRobot(sql)
    target = robot.generateSQL()
    fo.write(target)

                  

def CreateItemMatrix():
    dom = minidom.parse(fileXml)
    root = dom.documentElement
    itemList= []
    
    ItemDB_List.addTag('title')
    ItemDB_List.addTag('description')
    ItemDB_List.addTag('content:encoded')
    
    fo = open("./qpetLib/qpetData.sql","a") 
    robot = sqlRobot.SqlRobot()
    sql = 'insert into ItemMatrix (imgUrl, context, webUrl, itemId)  Values( ?, ?, ?, ?);\n'
    robot.createRobot(sql)   
    
    id = 0
    for eachItem in root.getElementsByTagName('item'):
        item = ItemDB_List()
        item.setItem(eachItem)
        itemList.append(item)
#        print '%s\n%s\n%s\n\n' %(item.title,item.url,item.imgUrl)
        
        robot.setParameter( 'STR',item.imgUrl)
        robot.setParameter( 'STR',item.title)
        robot.setParameter( 'STR',item.url)
        robot.setParameter( 'INT','%d' %id)
        
        sql = robot.generateSQL()
        id += 1
        sql.decode("utf-8").encode("gbk")     
        fo.write( sql )      

def main():
    pass

if __name__ == '__main__':
    main()