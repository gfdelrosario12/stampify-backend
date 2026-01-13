package com.stampify.passport.mappers;

import com.stampify.passport.models.Organization;
import com.stampify.passport.dto.OrganizationDTO;
import org.springframework.stereotype.Service;

@Service
public class OrganizationMapper {

    public OrganizationDTO toDTO(Organization org) {
        if (org == null) return null;

        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setDomain(org.getDomain());
        dto.setCreatedAt(org.getCreatedAt());
        dto.setUpdatedAt(org.getUpdatedAt());
        dto.setDeleted(org.isDeleted());

        return dto;
    }
}
