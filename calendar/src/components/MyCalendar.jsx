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
  const [form, setForm] = useState({ title: '', description: '' });

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
    setForm({ title: '', description: '' });
    setModalData({ show: true, dateStr, allDay });
    // show bootstrap modal (if you prefer native React modal, we can do that)
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
    openModalForDate(arg.dateStr, arg.allDay);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.title) return alert('Title required');

    const newEvent = {
      id: `${form.title}-${modalData.dateStr}-${Date.now()}`,
      title: form.title,
      start: modalData.dateStr,
      allDay: modalData.allDay,
      extendedProps: { description: form.description },
    };

    setEvents((prev) => [...prev, newEvent]);
    closeModal();
  };

  const handleEventDidMount = (info) => {
    // bootstrap tooltip for event title
    var tooltip = new bootstrap.Tooltip(info.el, {
      title: info.event.title,
      placement: 'top',
      trigger: 'hover',
      customClass: 'popoverStyle',
      container: 'body'
    });
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
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" data-bs-dismiss="modal" onClick={closeModal}>Cancel</button>
              <button type="submit" className="btn btn-primary">Add Event</button>
            </div>
          </form>
        </div>
      </div>

      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        headerToolbar={{
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay'
        }}
        height="88vh"
        aspectRatio={1}
        selectable={true}
        editable={true}
        dateClick={handleDateClick}
        events={events}
        eventDidMount={handleEventDidMount}
      />
    </>
  );
}

export default MyCalendar;