## this program will convert OSM files from OpenStreetMaps into a format required by the GRUVE infrastructure..
## it extracts nodes that are amenities like restaurants, bars, postboxes, etc.. 
open (IN, "edinburgh_map_full.osm");
open (OUT, ">map2.osm");
while(<IN>){
	if (m/\<node.*\/\>/ or m/\<\/node\>/ or m/\<way.*/){
		print OUT $_;
	} else {
		chomp;
		print OUT $_;
	}
}
close(IN);
close(OUT);

open (IN, "map2.osm");
open (OUT, ">map3.osm");
while (<IN>){
	if (m/amenity/ or m/traffic/){
		print OUT $_;
	}
}
close (IN);
close (OUT);

open (IN, "map3.osm");
open (OUT, ">map4.osm");
while (<IN>){
	$_ =~ s/k="capacity" v=".*?"//g;
	$_ =~ s/user=".*?" //g;
	$_ =~ s/uid=".*?" //g;
	$_ =~ s/visible=".*?" //g;
	$_ =~ s/version=".*?" //g;
	$_ =~ s/changeset=".*?" //g;
	$_ =~ s/timestamp=".*?">//g;
	$_ =~ s/<tag k="amenity" v="/type="/g;
	$_ =~ s/<tag k="cuisine" v="/cuisine="/g;
	$_ =~ s/<tag k="name" v="/name="/g;
	$_ =~ s/<tag k="highway" v="traffic_signals"/type="traffic_signals"/g;
	if (!m/type="traffic_signals"/){
		$_ =~ s/<tag k="crossing" v="traffic_signals"/type="traffic_signals"/g;
	}
	$_ =~ s/<tag .*?\/> //g;
	$_ =~ s/\/>//g;
	$_ =~ s/<\/node>/\/>/g;
	$_ =~ s/ +/ /g;
	$_ =~ s/node/entity/;
	if (!m/name=/) { 
		if (m/operator/){
			$_ =~ s/<tag k="operator" v="/name="/g;
		} else {
			$_ =~ s/\/>/ name=""\/>/;
		}
	}
	if (!m/name=/) { 
		if (m/operator/){
			$_ =~ s/<tag k="operator" v="/name="/g;
		} else {
			$_ =~ s/\/>/ name=""\/>/;
		}
	}
	if (m/type/) {
		print OUT $_;
	}
}
close(IN);
close(OUT);