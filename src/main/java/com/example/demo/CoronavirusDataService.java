package com.example.demo;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class CoronavirusDataService {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv"; 
	private List<LocationsStat> allStats = new ArrayList<>();
	
	
	
	public List<LocationsStat> getAllStats()
	{
		return allStats;
	}
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException
	{
		
		List<LocationsStat> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(VIRUS_DATA_URL))
				.build();
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		
		@SuppressWarnings("deprecation")
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		
		for (CSVRecord record : records) {

		    LocationsStat locationstat = new LocationsStat();
		    locationstat.setState(record.get("Province/State"));
		    locationstat.setCountry(record.get("Country/Region"));
		    locationstat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
		    int latestCases = Integer.parseInt(record.get(record.size()-1));
		    int prevDayCaeses = Integer.parseInt(record.get(record.size()-2));
		    locationstat.setDiffFromPrevDay(latestCases - prevDayCaeses);
		    
		    
		    newStats.add(locationstat);

		}
		allStats = newStats;
	}
}
