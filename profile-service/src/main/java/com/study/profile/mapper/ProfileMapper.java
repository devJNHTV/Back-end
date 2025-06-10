package com.study.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.study.profile.dto.request.RegistrationRequest;
import com.study.profile.dto.response.ProfileResponse;
import com.study.profile.entity.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(target = "profileId", ignore = true)
    Profile toProfile(RegistrationRequest request);

    ProfileResponse toProfileResponse(Profile profile);
}
