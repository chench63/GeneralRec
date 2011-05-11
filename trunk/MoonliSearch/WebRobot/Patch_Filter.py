#!/usr/bin/python

from string import find, lower

def httpFilter(eachlink):
	if eachlink.endswith('html'):
		return True
	elif eachlink.endswith('jsp'):
		return True
	elif eachlink.endswith('htm'):
		return True
	elif eachlink.endswith('php'):
		return True
	elif eachlink.endswith('asp'):
		return True
	elif eachlink.find('html?') > -1:
		return True
	elif eachlink.find('jsp?') > -1:
		return True
	elif eachlink.find('asp?') > -1:
		return True
	else:
		return False

def typeFilter(eachlink):
	if httpFilter(eachlink):
		return True
	else:
		return False


def nameFilter(url):
	if url.find('html?') > -1:
		return True
	elif url.find('jsp?') > -1:
		return True
	elif url.find('asp?') > -1:
		return True
	else:
		return False
