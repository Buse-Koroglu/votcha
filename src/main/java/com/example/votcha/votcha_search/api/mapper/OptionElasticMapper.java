package com.example.votcha.votcha_search.api.mapper;

import com.example.votcha.options.domain.model.Option;
import com.example.votcha.votcha_search.api.dto.data.OptionSyncData;
import com.example.votcha.votcha_search.domain.model.OptionDocument;
import org.springframework.stereotype.Component;

@Component
public class OptionElasticMapper {
    public OptionSyncData optionToOptionSyncData(Option option) {
        return OptionSyncData.builder()
                .voteCount(option.getVoteCount())
                .content(option.getContent())
                .id(option.getId())
                .build();
    }
    public OptionDocument optionToOptionDocument(Option option){
        return OptionDocument.builder()
                .id(option.getId())
                .content(option.getContent())
                .voteCount(option.getVoteCount())
                .build();
    }
    public OptionDocument optionSyncToOptionDocument(OptionSyncData opt){
        return OptionDocument.builder()
                .id(opt.id())
                .content(opt.content())
                .voteCount(opt.voteCount())
                .build();
    }
}
