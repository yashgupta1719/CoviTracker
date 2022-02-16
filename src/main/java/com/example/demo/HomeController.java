package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@Autowired
	private CoronavirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model)
	{
		List<LocationsStat> allStats = coronaVirusDataService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalReportedCases",totalReportedCases);
		
		return "home";
	}
}
