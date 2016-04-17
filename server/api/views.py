from django.shortcuts import render
from django.db.models import Q, F

from django.http import HttpResponse
from django.http import JsonResponse

from django.utils import timezone

from django.contrib.gis.geos import Point,fromstr,GEOSGeometry,Polygon,LineString
from django.contrib.gis.db.models.functions import Centroid,Distance
from django.contrib.gis.measure import D

from social.apps.django_app.default.models import UserSocialAuth

from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status

#from social_auth.db.django_models import UserSocialAuth

from geopy.distance import distance
from geopy import Point

from models import Event, User, SourceProfile, Upvoted
from serializers import EventSerializer, UserSerializer
import requests
import tweepy

auth = tweepy.OAuthHandler("AjIqttxIF7c3vc2uLwtfb4Re1","8Ag71z27wFitx9qRKQ16pMlF591Xc7MIWDrQ70cxBfEY8puJjW");
auth.set_access_token("1029855162-OYciEHy2fK8liPIINSkm0jGjtiaU6PWtuzb2HoV","1VYxG5QoT0K8YDwimglatxf755j4050L5L8Soa193XlP7")
api = tweepy.API(auth,parser=tweepy.parsers.JSONParser())

@api_view(['GET'])
def test(request,target=None):
	response_data={}
	response_data['result']='Test successful'
	return JsonResponse(response_data)

import random

@api_view(['GET'])
def create_event(request):
	entrylist = []
	for i in range(1,20000):
		#source = User.objects.get(username=request.GET.get('source',''))
		username = request.GET.get('source','')
		if username != '':
			source = User.objects.get(username=username)
		else:
			source = ''
		lat = request.GET.get('lat',32.7767+random.gauss(0,1))
		lon = request.GET.get('lon',-96.7970+random.gauss(0,1))

		point =  GEOSGeometry('SRID=4326;POINT(%f %f)' % (float(lon),float(lat)))

		localname = request.GET.get('localname','Event address')
		color = request.GET.get('color',random.choice(['blue','orange','indigo','brown','purple','green','pink']))
		name = request.GET.get('name','Name of event')
		short = request.GET.get('short','DEFAULT DESCRIPT')

		start = request.GET.get('start',None)
		stop = request.GET.get('stop',None)

		hashtag = request.GET.get('hashtag','#hackdfw')

		if start == None or stop == None:
			ev = Event(source=source,localname=localname,color=color,name=name,short=short,hashtag=hashtag,location=point,clicks=random.randint(1,3000))
		else:
			ev = Event(location=point,source=source,localname=localname,color=color,name=name,short=short,hashtag=hashtag,start=start,stop=stop)
		entrylist.append(ev)
	Event.objects.bulk_create(entrylist)
	return Response(EventSerializer(ev).data)

@api_view(['GET'])
def all_events(request):
	return Response(EventSerializer(Event.objects.all().order_by('-clicks')[:50],many=True).data)

@api_view(['GET'])
def points(request,target=None):
	tie = request.GET.get('tie',20)
	if target != None:
		return Response(EventSerializer(Event.objects.get(id=target)).data)
	radius = request.GET.get('radius',None)
	if radius != None:	
		lat = request.GET.get('lat',None)
		lon = request.GET.get('lon',None)
		if lat == None or lon == None:
			center = None
		else:
			center = GEOSGeometry('SRID=4326;POINT(%f %f)'% (float(lon),float(lat)))
	else:
		#bounding box on hold for now...
		bbox = (
				request.GET.get('lonl',None),
				request.GET.get('latl',None),
				request.GET.get('lonh',None),
				request.GET.get('lath',None)
				)
		if bbox[0] and bbox[1] and bbox[2] and bbox[3]:
			geom = fromstr('POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))'%(
				float(bbox[0]),float(bbox[1]),
				float(bbox[0]),float(bbox[3]),
				float(bbox[2]),float(bbox[3]),
				float(bbox[2]),float(bbox[1]),
				float(bbox[0]),float(bbox[1])),srid=4326)

			#linestring = LineString(Polygon(points))
		#linestring.transform(3857)
		#box = linestring.buffer(width=100)
		#box.transform(4326)

			center = geom.centroid
			centerp = Point(center.y,center.x)
			radius = max(
					distance(centerp,Point('%f %f'%(float(bbox[1]),float(bbox[0])))).meters,
					distance(centerp,Point('%f %f'%(float(bbox[3]),float(bbox[0])))).meters,
					distance(centerp,Point('%f %f'%(float(bbox[3]),float(bbox[2])))).meters,
					distance(centerp,Point('%f %f'%(float(bbox[1]),float(bbox[2])))).meters,)
	if radius != None and center != None:
		qset = Event.objects.filter(location__distance_lte=(center, D(m=radius))).extra(select ={'distance':'ST_Distance(location,ST_GeomFromText(%s))'},select_params=(str(center),))
		if tie:
			qset = qset.order_by('-clicks','distance')[:tie]
		return Response(EventSerializer(qset,many=True).data)
	return Response("No center/radius provided\n"+str(request.GET),status=status.HTTP_417_EXPECTATION_FAILED)

@api_view(['GET'])
def upvote(request,target=None):
	if target == None:
		return Response("[{value=-1}]")
	if Upvoted.objects.filter(user=request.user,event__id=target).count()>0:
		return Response("[{value=1}]")
	e=Event.objects.get(id=target)
	e.clicks+=1
	e.save()
	u=Upvoted(user=request.user,event=e)
	u.save()
	return Response("[{value=0}]")

@api_view(['GET'])
def isupvoted(request,target=None):
	if target == None:
		return Response("false")
	if Upvoted.objects.filter(user=request.user,event__id=target).count()>0:
		return Response("true")
	return Response("false")

from django.shortcuts import render_to_response, redirect, render
from django.contrib.auth import logout as auth_logout
from django.contrib.auth.decorators import login_required
# from django.template.context import RequestContext

#currently limited to 100
@api_view(['GET'])
def tweets(request,target=None):
	since_id = request.GET.get("since_id",1)
	rpp = request.GET.get("rpp",20)
	lTweets=api.search("#"+Event.objects.get(id=target).hashtag,since_id=since_id,rpp=rpp,show_user=True)["statuses"]	
	return Response(lTweets)

def login(request):
	return render(request, 'login.html')

def hi(request):
	return render(request, 'Hi.html')

@login_required(login_url='/')
def home(request):
	return render_to_response('home.html')


def logout(request):
	auth_logout(request)
	return redirect('/')

