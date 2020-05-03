class Station:
    """
    Representation of the stations
    
    Attributes:
    "stop_id","stop_code","stop_name","stop_desc","platform_code",
    "platform_name","stop_lat","stop_lon","stop_address","zone_id",
    "stop_url","level_id","location_type","parent_station","wheelchair_boarding"
    
    This class is used to organize all Stations so that they
    can easily be used to form the network nodes
    """ 

    def __init__(self, stop_id, stop_code, stop_name, stop_desc, platform_code, platform_name,\
                    stop_lat, stop_lon, stop_address, zone_id, stop_url, level_id, location_type,\
                    parent_station, wheelchair_boarding, municipality, on_street, at_street, vehicle_type):
        self.stop_id = stop_id
        self.stop_code = stop_code
        self.stop_name = stop_name
        self.stop_desc = stop_desc
        self.platform_code = platform_code
        self.platform_name = platform_name
        self.stop_lat = float(stop_lat)
        self.stop_lon = float(stop_lon)
        self.stop_address = stop_address
        self.zone_id = zone_id
        self.stop_url = stop_url
        self.level_id = level_id
        self.location_type = location_type
        self.parent_station = parent_station
        self.wheelchair_boarding = wheelchair_boarding
        self.municipality = municipality
        self.on_street = on_street
        self.at_street = at_street
        self.vehicle_type = vehicle_type

        # figure out if station is for subways
        if 'Line' in self.stop_desc:
            self.t_stop = True
        else:
            self.t_stop = False

        # figure out if station identified by parent station or stop id
        # bus stops will be identified by stop_id, 
        # T-stops will be identified by parent station
        if parent_station == '':
            self.identifier = stop_id
        else:
            self.identifier = parent_station
        
    def __str__(self):
        if self.parent_station == '':
            string = 'Name: ' + self.stop_name + '\nStop ID: ' + self.stop_id + \
                '\nCoordinates: ' + '(' + self.stop_lat + ', ' + self.stop_lon + ')'
        else:
            string = 'Name: ' + self.stop_name + '\nParent Station: ' + \
                self.parent_station + '\nCoordinates: ' + '(' + str(self.stop_lat) \
                + ', ' + str(self.stop_lon) + ')'
        return string

    def get_name(self):
        return self.stop_name
    
    def get_id(self):
        return self.identifier
    
    def get_parent_station(self):
        return self.parent_station
    
    def get_coordinates(self):
        return (self.stop_lon, self.stop_lat)
    
    def is_T(self):
        # station is a t-stop if parent station DNE
        return not (self.parent_station == '')
