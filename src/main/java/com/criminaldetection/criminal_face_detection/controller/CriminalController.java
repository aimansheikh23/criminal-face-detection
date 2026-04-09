package com.criminaldetection.criminal_face_detection.controller;


import com.criminaldetection.criminal_face_detection.dto.CriminalResponse;
import com.criminaldetection.criminal_face_detection.entity.Criminal;
import com.criminaldetection.criminal_face_detection.enums.Status;
import com.criminaldetection.criminal_face_detection.service.CriminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CriminalController {

    @Autowired
    private CriminalService criminalService;

    @PostMapping("/criminal")
    public String saveCriminalData(@RequestBody Criminal criminal){
        criminalService.addCriminalRecord(criminal);
        return "Criminal Record has been added successfully";
    }

    @PostMapping("/criminal/{id}/photo")
    public String saveCriminalPhoto(@PathVariable Integer id, @RequestParam String imageUrl, @RequestParam String  minioKey){
        criminalService.addCriminalPhoto(id, imageUrl, minioKey);
        return "Criminal photo has been added successfully";
    }

    @GetMapping("/criminal/{id}")
    public CriminalResponse getCriminalResponseById(@PathVariable Integer id){
        return criminalService.getCriminal(id);
    }

    @GetMapping("/criminal/status/{status}")
    public List<CriminalResponse> getCriminalResponseByStatus(@PathVariable Status status){
        return criminalService.getCriminalByStatus(status);
    }

    @PutMapping("/criminal/{id}/{status}")
    public String criminalNewStatus(@PathVariable Integer id, @PathVariable Status status){
        criminalService.updateCriminalStatus(id, status);
        return "Criminal Status has been updated successfully";
    }

    @DeleteMapping("/criminal/{id}")
    public String deleteCriminalDataById(@PathVariable Integer id){
        criminalService.deleteCriminalData(id);
        return "Criminal Data has been deleted successfully";
    }
}
