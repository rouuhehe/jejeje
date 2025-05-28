package com.dbp.legalcheck.domain.lawyer;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.lawyer.LawyerRequestDTO;
import com.dbp.legalcheck.dto.lawyer.LawyerResponseDTO;
import com.dbp.legalcheck.exception.lawyer.InsufficientPermissionsException;
import com.dbp.legalcheck.exception.lawyer.LawyerAlreadyExistsException;
import com.dbp.legalcheck.exception.lawyer.LawyerNotFoundException;
import com.dbp.legalcheck.exception.lawyer.UserNotLawyerException;
import com.dbp.legalcheck.infrastructure.lawyer.LawyerRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LawyerService {
    private final LawyerRepository lawyerRepository;
    private final ModelMapper modelMapper;

    public Lawyer getLawyerFromUser(User user) {
        return lawyerRepository.findById(user.getId())
                .orElseThrow(UserNotLawyerException::new);
    }

    // GET /api/lawyers
    public List<LawyerResponseDTO> listLawyers() {
        return lawyerRepository.findAll().stream()
                .map(LawyerResponseDTO::new)
                .toList();
    }

    // GET /api/lawyers/{id}
    public LawyerResponseDTO getLawyerById(UUID id) {
        Lawyer lawyer = lawyerRepository.findById(id)
                .orElseThrow(LawyerNotFoundException::new);

        return modelMapper.map(lawyer, LawyerResponseDTO.class);
    }

    // POST /api/lawyers
    public LawyerResponseDTO registerLawyer(LawyerRequestDTO request) {
        if (lawyerRepository.existsByEmail(request.getEmail())) {
            throw new LawyerAlreadyExistsException();
        }
        Lawyer lawyer = modelMapper.map(request, Lawyer.class);
        Lawyer saved = lawyerRepository.save(lawyer);
        return modelMapper.map(saved, LawyerResponseDTO.class);
    }

    // PUT /api/lawyers/{id}
    public LawyerResponseDTO updateLawyer(UUID id, LawyerRequestDTO request, User user) {
        Lawyer lawyer = lawyerRepository.findById(id)
                .orElseThrow(LawyerNotFoundException::new);

        if (!user.getRole().equals(UserRole.ADMINISTRADOR) &&
                !lawyer.getId().equals(user.getId())) {
            throw new InsufficientPermissionsException();
        }

        modelMapper.map(request, lawyer);
        return modelMapper.map(lawyerRepository.save(lawyer), LawyerResponseDTO.class);
    }
}
