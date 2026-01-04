package com.stampify.passport.mappers;

import com.stampify.passport.dto.*;
import com.stampify.passport.models.*;

public class UserMapper {

    /* ================= BASE ================= */

    private static void mapBase(User user, UserDTO dto) {
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());

        dto.setPasswordChangedAt(user.getPasswordChangedAt());
        dto.setActive(user.getActive());

        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setAccountStatus(user.getAccountStatus());

        // ✅ THIS IS THE FIX
        dto.setRole(resolveRole(user));
    }

    /* ================= ROLE ================= */

    private static String resolveRole(User user) {
        if (user instanceof Admin) return "ADMIN";
        if (user instanceof Member) return "MEMBER";
        if (user instanceof Scanner) return "SCANNER";
        return "UNKNOWN";
    }

    /* ================= ADMIN ================= */

    public static AdminDTO toAdminDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        mapBase(admin, dto);
        dto.setOrganizationId(admin.getOrganization().getId());
        dto.setCreatedAt(admin.getCreatedAt());
        return dto;
    }

    /* ================= MEMBER ================= */

    public static MemberDTO toMemberDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        mapBase(member, dto);
        dto.setOrganizationId(member.getOrganization().getId());
        dto.setJoinedAt(member.getJoinedAt());
        dto.setLeftAt(member.getLeftAt());
        dto.setCreatedAt(member.getCreatedAt());
        dto.setPassportCount(
                member.getPassports() == null ? 0 : member.getPassports().size()
        );
        return dto;
    }

    /* ================= SCANNER ================= */

    public static ScannerDTO toScannerDTO(Scanner scanner) {
        ScannerDTO dto = new ScannerDTO();
        mapBase(scanner, dto);
        dto.setDeviceIdentifier(scanner.getDeviceIdentifier());
        dto.setRegisteredAt(scanner.getRegisteredAt());
        dto.setStampCount(
                scanner.getStamps() == null ? 0 : scanner.getStamps().size()
        );
        return dto;
    }

    /* ================= POLYMORPHIC ================= */

    public static UserDTO toDTO(User user) {
        if (user instanceof Admin admin) return toAdminDTO(admin);
        if (user instanceof Member member) return toMemberDTO(member);
        if (user instanceof Scanner scanner) return toScannerDTO(scanner);
        throw new IllegalArgumentException("Unknown user type");
    }
}
