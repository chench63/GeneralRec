'''
Created on 2011-5-26

@author: ppiachen@gmail.com
'''



from sys import argv
from os import makedirs, unlink, sep
from os.path import dirname, exists, isdir, splitext
from string import replace, find, lower
from htmllib import HTMLParser
from urllib import urlretrieve
from urlparse import urlparse, urljoin
from formatter import DumbWriter, AbstractFormatter
from cStringIO import StringIO

class Retriever(object):
	def __init__(self,url):
		self.url = url
		self.file = self.filename(url)

	def filename(self, url, deffile='index.xml'):
		parsedurl = urlparse(url, 'http:', 0)
		path = parsedurl[1] + parsedurl[2]
		ext = splitext(path)
		if ext[1] == '':
			if path[-1] == '/':
				path += deffile
			else:
				path += '/'+deffile
		ldir =  dirname(path)
		if sep != '/':
			ldir = replace(ldir, '/', sep)
		if not isdir(ldir):
			if exists(ldir): unlink(ldir)
			makedirs(ldir)
		return path

	def download(self):
		try:
			retval = urlretrieve(self.url, self.file)
		except IOError:
			retval = ('***Error***  "%s"' %self.url , None)
		return retval


class Crawler(object):
	count = 0
	def getPage(self, url):
		r =  Retriever(url)
		retval =  r.download()

		if retval[0] == '*':
			print retval, '...Skipping parse'
			return
		Crawler.count += 1
		print '\n(' , Crawler.count, ')'
		print 'URL: ', url
		print 'FILE: ', retval[0]

def main():
	if len(argv) > 1:
		url = argv[1]
	else:
		try:
			url = raw_input('Enter starting URL: ')
		except (KeyboardInterrupt, EOFError):
			url = ''

	if not url: return 
	robot = Crawler()
	robot.getPage(url)
	print 'Successfully Download!'

if __name__ == '__main__':
	main()

				
