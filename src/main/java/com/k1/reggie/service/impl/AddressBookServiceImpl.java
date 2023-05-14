package com.k1.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.k1.reggie.entity.AddressBook;
import com.k1.reggie.mapper.AddressBookMapper;
import com.k1.reggie.service.AddressBookService;
import com.k1.reggie.entity.AddressBook;
import com.k1.reggie.mapper.AddressBookMapper;
import com.k1.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
