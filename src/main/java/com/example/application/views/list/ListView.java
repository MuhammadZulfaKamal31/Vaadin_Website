package com.example.application.views.list;

import org.springframework.context.annotation.Scope;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CrmService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

//ketik aja komponen
//ini supaya uji test listview
@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Contact || Vaadin CRM")
// ketika di muat layout bakal jadi tata letak utama
@Route(value = "", layout = MainLayout.class)
// semua pngguna yang masuk boleh akses ini
@PermitAll
public class ListView extends VerticalLayout {
    // Contact ambil dari kalass tabelnya
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    // pasang kelas from nya disini, perhatikan tulisan form
    ContactForm form;
    // intial service
    private CrmService service;

    // semua tampilan tampil disini jadi ini khsusu pemanggilan
    public ListView(CrmService service) {
        // untuk mengunakan service
        this.service = service;
        addClassName("list-view");
        setSizeFull();
        // tinggal tekan aja buat methodenya
        configureGrid();
        configureForm();

        add(
                getToolbar(),
                getContent());
        // buat methode dulu disini biasanya
        updateList();
        closeEditor();

    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllContacts(filterText.getValue()));
    }

    private Component getContent() {

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureForm() {
        // panggil service nya disini juga, didalam contact Form
        form = new ContactForm(service.findAllCompanies(), service.findAllStatuses());
        form.setWidth("25em");

        // this:: ini tandanya memanggil sebuah methode, dan event Type dati klass
        // contactFROM dan fungsi :: juga cocok digunkan di listener soalnya gak
        // langsung ke panggil kalau gak di klik
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());
    }

    // melakukan save lalu tutup
    private void saveContact(ContactForm.SaveEvent event) {
        service.saveContact(event.getContact());
        updateList();
        closeEditor();
    };

    // melakukan delete lalu tutup
    private void deleteContact(ContactForm.DeleteEvent event) {
        service.deleteContact(event.getContact());
        updateList();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter By Name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        // panggil methode ipdate listnya disini
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add contact");
        addContactButton.addClickListener(e -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    // untuk nampilin form ketika pecet add
    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        // harus sama dengan nama columnya
        grid.setColumns("firstName", "lastName", "email");

        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e -> editContact(e.getValue()));
    }

    // ketika data di klik akan mucul formnya dan mengklik yang maka form menghilang
    private void editContact(Contact contact) {
        if (contact == null) {
            closeEditor();
        } else {
            form.setContact(contact);
            form.setVisible(true);
            addClassName("editing");

        }
    }

}
