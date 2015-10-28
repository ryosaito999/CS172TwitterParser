#Import the necessary methods from tweepy library
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream

consumer_key = "DR2sdWrsfQBWw6jVapZA9N6kN"
consumer_secret = "PoAOScmZOWpGkZ6qJoz6tIZn1Q7NdzieoGwQwlQmQsJObQLN5M"

access_token = "3941462833-jwJS09AoJz1cRTpT7jzjQBJtjLA2ukRjYxzPKGH"
access_token_secret = "wcGXUWREbycIREGJOk4ZtdnsWDHGXTTG6WLZKlR5ppEaC"


class StdOutListener(StreamListener):

    def on_data(self, data):
    	print data
    	return True
	
	def on_error(self, status_code):
		print "Error: " + repr(status_code)
	# 	return True # False to stop

    def on_limit(self, track):
    	print "!!! Limitation notice received: %s" % str(track)
    	return

	def on_timeout(self):
		print 'Timeout... Quitting'
		return True


l = StdOutListener()
auth = OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)
stream = Stream(auth, l)

#This line filter Twitter Streams to capture data by the keywords: 'python', 'javascript', 'ruby'
print 'Starting....'

stream.filter(locations=[-180,-90,180,90])
