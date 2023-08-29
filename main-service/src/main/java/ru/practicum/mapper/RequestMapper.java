package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.FullRequestDto;
import ru.practicum.dto.RequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static FullRequestDto toFullRequestDto(Request request) {
        return new FullRequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent(),
                UserMapper.toUserShortDto(request.getRequester()),
                request.getStatus()
        );
    }


    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }

    public static Request toDtoRequest(RequestDto requestDto, User user, Event event) {
       /* DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");*/
        return new Request(
                requestDto.getId(),
                //LocalDateTime.parse(requestDto.getCreated().toString(), formatter),
                requestDto.getCreated(),
                event,
                //requestDto.getEvent(),
                user,
                requestDto.getStatus()
        );

    }
}
