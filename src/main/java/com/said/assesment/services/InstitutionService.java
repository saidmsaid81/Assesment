package com.said.assesment.services;

import com.said.assesment.models.Institution;
import com.said.assesment.models.ResponseObject;
import com.said.assesment.repositories.InstitutionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class InstitutionService {

    private final InstitutionRepository mInstitutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        mInstitutionRepository = institutionRepository;
    }

    public ResponseObject addNewInstitution(String name) {
        if (mInstitutionRepository.checkIfInstitutionExistsByName(name)) {
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Adding Institution failed. An Institution with the same name already exists.",
                    new ArrayList<>()
            );
        }

        Institution institution = new Institution(name);
        mInstitutionRepository.save(institution);
        return new ResponseObject(HttpStatus.CREATED.value(),
                "Institution was successfully added",
                new ArrayList<>()
        );

    }

    public ResponseObject listAllInstitutions() {
        return new ResponseObject(HttpStatus.OK.value(),
                "Retrieving all institutions was successful",
                mInstitutionRepository.findAll()
        );
    }

    public ResponseObject searchInstitutions(String keyword) {
        return new ResponseObject(HttpStatus.OK.value(),
                "Searching institutions was successful",
                mInstitutionRepository.search(keyword)
        );
    }

    public ResponseObject sortInstitutionsByName(Sort.Direction direction) {
        return new ResponseObject(HttpStatus.OK.value(),
                "Sorting institutions was successful",
                mInstitutionRepository.findAll(Sort.by(direction, "name"))
        );
    }

    @Transactional
    public ResponseObject editInstitutionName(String newName, String oldName) {

        if (!mInstitutionRepository.checkIfInstitutionExistsByName(oldName)) {
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Editing Institution failed. The institution you are editing does not exist in the system.",
                    new ArrayList<>()
            );
        }

        if (mInstitutionRepository.checkIfInstitutionExistsByName(newName)) {
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Editing Institution failed. An Institution with the same name already exists.",
                    new ArrayList<>()
            );
        }

        mInstitutionRepository.updateInstitutionName(newName, oldName);
        return new ResponseObject(HttpStatus.OK.value(),
                "Institution name was successfully edited",
                new ArrayList<>()
        );

    }

    public ResponseObject deleteInstitution(String name) {
        Institution institution = mInstitutionRepository.getInstitutionByNameIgnoreCase(name);

        if (institution == null)
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Deleting institution failed.  The institution you are trying to delete does not exist in the " +
                            "system.",
                    new ArrayList<>());

        mInstitutionRepository.delete(institution);
        return new ResponseObject(HttpStatus.OK.value(),
                "Institution was successfully deleted.",
                new ArrayList<>());

    }

    public boolean checkInstitutionExists(String institutionName) {
        return mInstitutionRepository.checkIfInstitutionExistsByName(institutionName);
    }
}
