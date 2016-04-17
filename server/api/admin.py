from django.contrib import admin
from models import Event,SourceProfile,Upvoted
from django.contrib.gis import admin


admin.site.register(Event,admin.GeoModelAdmin)
admin.site.register(SourceProfile)
admin.site.register(Upvoted)


# Register your models here.
