package com.example.votify_meet.options.api.mapper;


import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.domain.model.Option;
import org.springframework.stereotype.Component;

@Component
public class OptionMapper {
    public OptionResponseDto toResponse(Option entity){
        return OptionResponseDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .build();
    }
    public Option toEntity(OptionRequestDto request){
        return Option.builder().content(request.content()).build();
    }
    private String normalize(String value){
        if(value != null && value.trim().isEmpty()){
            return null;
        }
        return value;
    }
    public void update(OptionRequestDto request,Option entity){
        String content = normalize(request.content());
        if(content != null){
            entity.setContent(content);
        }
    }


}
