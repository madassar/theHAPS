from rest_framework import serializers
from django.core.serializers import serialize
from api.models import User,SourceProfile,Event

class SourceSerializer(serializers.ModelSerializer):
	class Meta:
		model = SourceProfile
		fields = ('logo',)

class UserSerializer(serializers.ModelSerializer):
	profile = SourceSerializer(read_only=True)
	class Meta:
		model = User
		fields = ('first_name','profile')

class EventSerializer(serializers.ModelSerializer):
	source = UserSerializer(read_only=True)
	distance = serializers.FloatField(read_only=True)
	class Meta:
		model = Event
		fields = ('id','location','localname','source','priority','clicks','color','name','short','start','stop','distance')

