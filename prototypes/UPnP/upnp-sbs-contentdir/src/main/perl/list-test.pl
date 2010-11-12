    use Net::UPnP::ControlPoint;
    use Net::UPnP::AV::MediaServer;

$Devel::Trace::TRACE = 0;   # Disable
    my $obj = Net::UPnP::ControlPoint->new();

    @dev_list = $obj->search(st =>'upnp:rootdevice', mx => 3);

    $devNum= 0;
    foreach $dev (@dev_list) {
        $device_type = $dev->getdevicetype();
        if  ($device_type ne 'urn:schemas-upnp-org:device:MediaServer:1') {
				print "[$devNum] : " . $dev->getfriendlyname() . "\n";
            next;
        }
        unless ($dev->getservicebyname('urn:schemas-upnp-org:service:ContentDirectory:1')) {
            next;
        }
        $mediaServer = Net::UPnP::AV::MediaServer->new();
        $mediaServer->setdevice($dev);
        @content_list = $mediaServer->getcontentlist(ObjectID => "0/1/0");
        foreach $content (@content_list) {
            print_content($mediaServer, $content, 1);
        }
        $devNum++;
    }

    sub print_content {
        my ($mediaServer, $content, $indent) = @_;
        my $id = $content->getid();
        my $title = $content->gettitle();
        
        my $tabs ="";
        for ($n=0; $n<$indent; $n++) {
            $tabs.="\t";
        }
        print $tabs;
        print "$id = $title";
        if ($content->isitem()) {
            print " (" . $content->geturl();
            if (length($content->getdate())) {
                print " - " . $content->getdate();
            }
            print " - " . $content->getcontenttype() . ")";
        }
        print "\n";
        unless ($content->iscontainer()) {
				print $tabs;
				print("not a container\n");
            return;
        }
        @child_content_list = $mediaServer->getcontentlist(ObjectID => $id );
        if (@child_content_list <= 0) {
				print $tabs;
				print("no child in getcontent list\n");
            return;
        }
        
        $indent++;  
        return;  
        print $tabs;
        print("XXXXXXXXXXXXXX content called ",  $#child_content_list, "\n");
        foreach my $child_content (@child_content_list) {
				print $tabs;
				print("XXXXXXXXXXXXXX calling print_content ",  $child_content->getid(), " level: $indent", "\n");
            print_content($mediaServer, $child_content, $indent);
        }
    }