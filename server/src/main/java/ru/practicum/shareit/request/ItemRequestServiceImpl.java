package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto createDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = new ItemRequest();
        request.setDescription(createDto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        request = requestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(request, List.of());
    }

    @Override
    public List<ItemRequestDto> findAllByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorId(userId, Sort.by(Sort.Direction.DESC, "created"));

        return enrichWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> findAllOther(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(userId, Sort.by(Sort.Direction.DESC, "created"));

        return enrichWithItems(requests);
    }

    @Override
    public ItemRequestDto findById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<ItemShortDto> items = itemRepository.findAllByRequestIdIn(List.of(requestId)).stream()
                .map(item -> new ItemShortDto(item.getId(), item.getName()))
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    private List<ItemRequestDto> enrichWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<ItemShortDto>> itemsByRequest = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(item -> new ItemShortDto(item.getId(), item.getName()), Collectors.toList())
                ));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(
                        request,
                        itemsByRequest.getOrDefault(request.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }
}