from django.conf.urls import patterns, include, url
from django.contrib import admin

import api.views

urlpatterns = [
		#Test for connection...response should be clear.
	url(r'^test$', api.views.test),
	#Three methods to access markers:
	# GET /api/points?lat=23.523&lon=-88.632&radius=10000
	# GET /api/points?latl=22.21&lath=23.542&lonl=-93.452&lonh=-91.345&tie=30
	# GET /api/points/4256
	# (lat,lon,radius,?tie) returns tie (or 20) points within radius of (lat,lon)
	# (latl,lath,lonl,lonh,?tie) returns tie points within the defined box
	# /api/points/id returns the Event with the given id
	url(r'^points$', api.views.points),
	url(r'^points/(?P<target>\w+)$',api.views.points),
	#  /api/hi pulls up a silly embedded map
	url(r'^hi$', api.views.hi),
	#  /api/tweets/id 
	# ?since_id returns tweets starting after the id number given
	# ?rpp returns rpp (up to 100) tweets per query
	url(r'^tweets/(?P<target>\w+)', api.views.tweets),
	# populates the database with 20000 fake entries distributed around Dallas
	url(r'^create_event$', api.views.create_event),
	# /api/upvote/id upvotes Event(id=id) if not already upvoted.
	url(r'^upvote/(?P<target>\w+)$',api.views.upvote),
	# /api/upvote/id returns whether upvoted by user
	url(r'^isupvoted/(?P<target>\w+)$',api.views.isupvoted),
]

