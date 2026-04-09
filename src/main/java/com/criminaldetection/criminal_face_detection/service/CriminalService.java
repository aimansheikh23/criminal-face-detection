package com.criminaldetection.criminal_face_detection.service;


import com.criminaldetection.criminal_face_detection.dto.CriminalResponse;
import com.criminaldetection.criminal_face_detection.entity.Criminal;
import com.criminaldetection.criminal_face_detection.entity.CriminalPhoto;
import com.criminaldetection.criminal_face_detection.enums.Status;
import com.criminaldetection.criminal_face_detection.exception.ResourceNotFoundException;
import com.criminaldetection.criminal_face_detection.repository.CriminalPhotoRepository;
import com.criminaldetection.criminal_face_detection.repository.CriminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CriminalService {

    @Autowired
    private CriminalRepository  criminalRepository;

    @Autowired
    private CriminalPhotoRepository criminalPhotoRepository;

    public void addCriminalRecord(Criminal criminal){
        criminalRepository.save(criminal);
    }

    public void addCriminalPhoto(Integer criminal_id, String imageUrl, String minioKey){

        Criminal criminal = criminalRepository.findById(criminal_id).orElseThrow(() -> new ResourceNotFoundException("Criminal not found"));

        CriminalPhoto photo = new CriminalPhoto();
        photo.setCriminal(criminal);
        photo.setImageUrl(imageUrl);
        photo.setMinioObjectKey(minioKey);
        criminalPhotoRepository.save(photo);
    }

    public CriminalResponse getCriminal(Integer criminal_id){
        Criminal criminalData = criminalRepository.findById(criminal_id).orElseThrow(() -> new ResourceNotFoundException("Criminal Record not Found"));
        return new CriminalResponse(criminalData.getFull_name(), criminalData.getCrimeType(), criminalData.getStatus(), criminalData.getAlias());
    }

    public List<CriminalResponse> getCriminalByStatus(Status status){
        List<Criminal> criminal = criminalRepository.findByStatus(status);
        List<CriminalResponse> responses = new ArrayList<>();
        for(Criminal criminalData : criminal){
            responses.add(new CriminalResponse(criminalData.getFull_name(), criminalData.getCrimeType(), criminalData.getStatus(), criminalData.getAlias()));
        }
        return responses;
    }

    public void updateCriminalStatus(Integer criminal_id, Status newStatus){
        Criminal criminal = criminalRepository.findById(criminal_id).orElseThrow(()-> new ResourceNotFoundException("Criminal not found"));
        criminal.setStatus(newStatus);
        criminalRepository.save(criminal);
    }

    public void deleteCriminalData(Integer id){
        Criminal criminal = criminalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Criminal not found"));
        List<CriminalPhoto> criminalPhotos = criminalPhotoRepository.findByCriminalId(id);

        criminalPhotoRepository.deleteAll(criminalPhotos);
        criminalRepository.delete(criminal);
    }
}
