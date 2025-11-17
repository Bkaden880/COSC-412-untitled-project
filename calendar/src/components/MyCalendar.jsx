import React, { useRef, useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import * as bootstrap from 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

const STORAGE_KEY = 'mycalendar.events.v1';

function MyCalendar() {
  const calendarRef = useRef(null);
  const [events, setEvents] = useState([]);
  const [modalData, setModalData] = useState({ show: false, dateStr: '', allDay: true });
  const [form, setForm] = useState({ title: '', description: '', date: '', time: '12:00', endDate: '', endTime: '13:00', allDay: true });

  // load events from localStorage on mount
  useEffect(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) setEvents(JSON.parse(raw));
    } catch (e) {
      console.warn('Failed to load events from storage', e);
    }
  }, []);

  // save events to localStorage when they change
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(events));
    } catch (e) {
      console.warn('Failed to save events to storage', e);
    }
  }, [events]);

  const openModalForDate = (dateStr, allDay = true) => {
    // initialize form date/time
    const dateOnly = dateStr ? dateStr.split('T')[0] : new Date().toISOString().slice(0, 10);
    // default end time one hour later
    const defaultEndTime = (() => {
      const [h, m] = ['12', '00'];
      const hh = String((parseInt(h, 10) + 1) % 24).padStart(2, '0');
      return `${hh}:${m}`;
    })();
  setForm({ title: '', description: '', date: dateOnly, time: '12:00', endDate: dateOnly, endTime: defaultEndTime, allDay });
  setModalData({ show: true, dateStr: dateOnly, allDay, editingId: null });
    // show bootstrap modal (if you prefer native React modal, we can do that)
    const modalEl = document.getElementById('eventModal');
    const bsModal = new bootstrap.Modal(modalEl);
    bsModal.show();
  };

  const openModalForEvent = (eventApi) => {
    // populate form from event api (FullCalendar EventApi)
    const start = eventApi.start;
    const end = eventApi.end;
    // read local date/time components to avoid UTC shifts when editing
    const pad = (v) => String(v).padStart(2, '0');
    const date = start ? `${start.getFullYear()}-${pad(start.getMonth()+1)}-${pad(start.getDate())}` : '';
    const time = start ? `${pad(start.getHours())}:${pad(start.getMinutes())}` : '12:00';
    const endDate = end ? `${end.getFullYear()}-${pad(end.getMonth()+1)}-${pad(end.getDate())}` : '';
    const endTime = end ? `${pad(end.getHours())}:${pad(end.getMinutes())}` : '';
    setForm({
      title: eventApi.title || '',
      description: (eventApi.extendedProps && eventApi.extendedProps.description) || '',
      date,
      time,
      endDate: endDate || date,
      endTime: endTime || '',
      allDay: !!eventApi.allDay,
    });
    setModalData({ show: true, dateStr: date, allDay: !!eventApi.allDay, editingId: eventApi.id });
    const modalEl = document.getElementById('eventModal');
    const bsModal = new bootstrap.Modal(modalEl);
    bsModal.show();
  };

  const closeModal = () => {
    const modalEl = document.getElementById('eventModal');
    const bsModal = bootstrap.Modal.getInstance(modalEl);
    if (bsModal) bsModal.hide();
    setModalData({ show: false, dateStr: '', allDay: true });
  };

  const handleDateClick = (arg) => {
    // FullCalendar passes dateStr like '2025-11-09' for dates
    openModalForDate(arg.dateStr, arg.allDay);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.title) return alert('Title required');

    // determine start string. If allDay, use date only (YYYY-MM-DD). If time provided, use YYYY-MM-DDTHH:MM
    const start = form.allDay || !form.time ? form.date : `${form.date}T${form.time}`;

    // helper to add one hour if no end provided for timed events
    const addOneHourISO = (dateStr, timeStr) => {
      // create local Date from components to avoid timezone shifts
      const [y, m, d] = dateStr.split('-').map(Number);
      const [hh, mm] = (timeStr || '12:00').split(':').map(Number);
      const dt = new Date(y, m - 1, d, hh, mm);
      dt.setHours(dt.getHours() + 1);
      const Y = dt.getFullYear();
      const M = String(dt.getMonth() + 1).padStart(2, '0');
      const D = String(dt.getDate()).padStart(2, '0');
      const H = String(dt.getHours()).padStart(2, '0');
      const Min = String(dt.getMinutes()).padStart(2, '0');
      return `${Y}-${M}-${D}T${H}:${Min}`;
    };

    // compute end
    let end;
    if (form.endDate) {
      end = form.allDay || !form.endTime ? form.endDate : `${form.endDate}T${form.endTime}`;
    } else {
      if (!form.allDay) {
        // default to one hour after start
        end = addOneHourISO(form.date, form.time);
      }
    }

    // if editing an existing event, update it
    if (modalData && modalData.editingId) {
      setEvents((prev) => prev.map((ev) => ev.id === modalData.editingId ? ({ ...ev, title: form.title, start, end, allDay: !!form.allDay, extendedProps: { description: form.description } }) : ev));
    } else {
      const newEvent = {
        id: `${form.title}-${start}-${Date.now()}`,
        title: form.title,
        start,
        end,
        allDay: !!form.allDay,
        extendedProps: { description: form.description },
      };
      setEvents((prev) => [...prev, newEvent]);
    }
    closeModal();
  };

  const handleDelete = () => {
    if (modalData && modalData.editingId) {
      setEvents((prev) => prev.filter((ev) => ev.id !== modalData.editingId));
    }
    closeModal();
  };

  const handleEventDidMount = (info) => {
    // intentionally left blank to avoid showing the default tooltip/tag on hover
  };

  return (
    <>
      {/* Modal HTML (Bootstrap) */}
      <div className="modal fade" id="eventModal" tabIndex="-1" aria-hidden="true">
        <div className="modal-dialog">
          <form className="modal-content" onSubmit={handleSubmit}>
            <div className="modal-header">
              <h5 className="modal-title">Add Event - {modalData.dateStr}</h5>
              <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={closeModal}></button>
            </div>
            <div className="modal-body">
              <div className="mb-3">
                <label className="form-label">Title</label>
                <input className="form-control" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
              </div>
              <div className="mb-3">
                <label className="form-label">Description</label>
                <textarea className="form-control" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
              </div>
              <div className="mb-3">
                <label className="form-label">Date</label>
                <input type="date" className="form-control" value={form.date} onChange={(e) => setForm({ ...form, date: e.target.value })} required />
              </div>
              <div className="mb-3 d-flex gap-2 align-items-center">
                <div style={{ flex: 1 }}>
                  <label className="form-label">Time</label>
                  <input type="time" className="form-control" value={form.time} onChange={(e) => setForm({ ...form, time: e.target.value })} disabled={form.allDay} />
                </div>
                <div style={{ minWidth: 140 }}>
                  <label className="form-label">All day</label>
                  <div>
                    <input id="allDay" type="checkbox" checked={form.allDay} onChange={(e) => setForm({ ...form, allDay: e.target.checked })} />
                    <label htmlFor="allDay" style={{ marginLeft: 8 }}> All day</label>
                  </div>
                </div>
              </div>
              <div className="mb-3 d-flex gap-2 align-items-center">
                <div style={{ flex: 1 }}>
                  <label className="form-label">End date</label>
                  <input type="date" className="form-control" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
                </div>
                <div style={{ minWidth: 140 }}>
                  <label className="form-label">End time</label>
                  <input type="time" className="form-control" value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })} disabled={form.allDay} />
                </div>
              </div>
            </div>
            <div className="modal-footer">
              {modalData && modalData.editingId && (
                <button
                  type="button"
                  className="btn btn-danger me-auto"
                  onClick={() => {
                    if (window.confirm('Delete this event?')) {
                      handleDelete();
                    }
                  }}
                >
                  Delete
                </button>
              )}
              <button type="button" className="btn btn-secondary" data-bs-dismiss="modal" onClick={closeModal}>Cancel</button>
              <button type="submit" className="btn btn-primary">{modalData && modalData.editingId ? 'Save' : 'Add Event'}</button>
            </div>
          </form>
        </div>
      </div>

      {/* FullCalendar toolbar custom button will provide Add Event on the header, so we no longer render it here */}

      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        customButtons={{
          addEventButton: {
            text: '+ Add Event',
            click: () => openModalForDate(new Date().toISOString().slice(0,10), false),
          },
        }}
        headerToolbar={{
          left: 'prev,next today',
          center: 'title',
          right: 'addEventButton dayGridMonth,timeGridWeek,timeGridDay'
        }}
        eventClick={(arg) => {
          // open modal populated for editing
          openModalForEvent(arg.event);
        }}
        height="88vh"
        aspectRatio={1}
        selectable={true}
        editable={true}
        dateClick={handleDateClick}
        events={events}
        eventBackgroundColor="#007bff"
        eventBorderColor="#0056b3"
        eventTextColor="#ffffff"
        eventDidMount={handleEventDidMount}
      />
    </>
  );
}

export default MyCalendar;