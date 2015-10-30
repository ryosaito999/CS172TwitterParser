#Import the necessary methods from tweepy library
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
import sys
import re
import threading
import json
import urllib
import urllib2
from BeautifulSoup import BeautifulSoup
import codecs
import sys




consumer_key = "DR2sdWrsfQBWw6jVapZA9N6kN"
consumer_secret = "PoAOScmZOWpGkZ6qJoz6tIZn1Q7NdzieoGwQwlQmQsJObQLN5M"

access_token = "3941462833-jwJS09AoJz1cRTpT7jzjQBJtjLA2ukRjYxzPKGH"
access_token_secret = "wcGXUWREbycIREGJOk4ZtdnsWDHGXTTG6WLZKlR5ppEaC"

file_name = "tweets.txt"

 
class FuncThread(threading.Thread):
    def __init__(self, target, *args):
        self._target = target
        self._args = args
        threading.Thread.__init__(self)
 
    def run(self):
        self._target(*self._args)

def unicodeConvert(text):
    try:
        text = unicode(text, 'utf-8')
    except TypeError:
        if text:
            return text
        else:
            return "None"
 
    # Example usage
def printTweet(data):
    print data

    #check data here!
    #run all strings inside .html checker
    listTokens = data.split("\"")
    for s in listTokens:
        match = re.search(r'(https|http).*(\.html|\.htm)', s)
      
        if match:
            link = match.group(0).replace("\\" , "")    
            print "HTML_LINK: %s" % link 

            try:
                response = urllib2.urlopen(link)
            except Exception, e:
                print "403"
            else:
                soup = BeautifulSoup(response.read())
                title = soup.find('title')
             
                if title:
                    title = unicode(title)
                    print "HTML_TITLE: %s" % title




class StdOutListener(StreamListener):

    def on_data(self, data):
    	#data contains all data about each tweet. Parse this in a new thread
        # t1 = FuncThread(printTweet, data)
        # t1.start()
        # t1.join()
        printTweet(data)
        return True

    def on_error(self, status_code):
		print "Error: " + repr(status_code)
		return True # False to stop

    def on_limit(self, track):
    	print "!!! Limitation notice received: %s" % str(track)
    	return

	# def on_timeout(self):
	# 	print 'Timeout... Quitting'
	# 	return True


print 'Starting....'

#FILL TEXT FILE WITH TWEETS
orig_stdout = sys.stdout



l = StdOutListener()
auth = OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)
stream = Stream(auth, l)

#Filter for all tweets in all locations of the globe
stream.filter(locations=[-180,-90,180,90])
# stream.filter(track=['python', 'javascript', 'ruby'])


sys.stdout = orig_stdout
f.close()

print 'Closed!....'


