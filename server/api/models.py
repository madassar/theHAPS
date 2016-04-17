from __future__ import unicode_literals

from django.db import models
from django.utils import timezone
from django.contrib.gis.db import models
from django.contrib.auth.models import User
from django.contrib.gis.geos import Point

from django.utils.translation import gettext as _

timezone.make_aware(timezone.datetime.max, timezone.get_default_timezone())

class SourceProfile(models.Model):
	user = models.OneToOneField(User, primary_key=True, db_index=True, related_name="profile")
	logo = models.ImageField(upload_to='/home/django/dfwhack/server/media/',null=True)
	objects = models.GeoManager()

class Event(models.Model):
	priority = models.IntegerField(default=0)
	interest = models.IntegerField(default=0)
	clicks = models.IntegerField(default=0);
	source = models.ForeignKey(User, null=True, db_index=True, related_name="source")
	location = models.PointField(geography=True, dim=2, srid=4326, default=Point(0.0,0.0))
	localname = models.CharField(max_length=75)
	objects = models.GeoManager()
	COLOR_CHOICES = (
			('blue','blue'),
			('orange','orange'),
			('indigo','indigo'),
			('brown','brown'),
			('purple','purple'),
			('green','green'),
			('pink','pink'))
	color = models.CharField(choices=COLOR_CHOICES,default='pink',max_length=8)	# hex color value possibly with 0x
	name = models.CharField(max_length=75)
	short = models.CharField(default="",max_length=240)
	start = models.DateTimeField(default=timezone.now)
	stop = models.DateTimeField(default=timezone.datetime.max)
	hashtag = models.CharField(max_length=30)

class Upvoted(models.Model):
	user = models.ForeignKey(User, null=False,db_index=True)
	event= models.ForeignKey(Event,null=False,db_index=True)
	
