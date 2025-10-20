import threading
import time
import random


class ThreadGroup:
    def __init__(self, name, parent=None):
        self.name = name
        self.parent = parent
        self.threads = []
        self.max_priority = 10
        
    def add_thread(self, thread):
        self.threads.append(thread)
        
    def set_max_priority(self, priority):
        self.max_priority = priority
        
    def list_threads(self):
        print(f"\nThreadGroup [{self.name}]:")
        print(f"  Max Priority: {self.max_priority}")
        for thread in self.threads:
            status = "alive" if thread.is_alive() else "dead"
            print(f"  Thread[{thread.name}] - Priority: {getattr(thread, 'priority', 'N/A')} - Status: {status}")


class CustomThread(threading.Thread):
    def __init__(self, target=None, name=None, thread_group=None, priority=5):
        super().__init__(target=target, name=name)
        self.priority = priority
        self.thread_group = thread_group
        if thread_group:
            thread_group.add_thread(self)
            
    def set_priority(self, priority):
        self.priority = priority
        
    def run(self):
        if self._target:
            self._target()
        else:
            print(f"Salut de la firul {self.name} cu prioritatea {self.priority}...")
            time.sleep(0.5)


def main():
    print("=== Python Threading Example ===")
    
    current_thread = threading.current_thread()
    print(f"Current thread: {current_thread.name}")
    
    sys_group = ThreadGroup("System")
    
    print("\n--- Creating threads in system group ---")
    th1 = CustomThread(name="Th1", thread_group=sys_group, priority=7)
    th2 = CustomThread(name="Th2", thread_group=sys_group, priority=7)
    tha = CustomThread(name="ThA", thread_group=sys_group, priority=3)
    
    th1.start()
    th2.start()
    tha.start()
    
    time.sleep(0.1)
    sys_group.list_threads()
    
    print("\n--- Creating ThreadGroup G1 and G3 ---")
    g1 = ThreadGroup("G1")
    g1.set_max_priority(10)
    
    g3 = ThreadGroup("G3", parent=g1)
    g3.set_max_priority(10)
    
    tha_g3 = CustomThread(name="Tha", thread_group=g3, priority=3)
    thb_g3 = CustomThread(name="Thb", thread_group=g3, priority=3)
    thc_g3 = CustomThread(name="Thc", thread_group=g3, priority=3)
    thd_g3 = CustomThread(name="Thd", thread_group=g3, priority=3)
    

    tha_g3.start()
    thb_g3.start()
    thc_g3.start()
    thd_g3.start()
    
    time.sleep(0.1)
    g3.list_threads()
    
    print("\n--- Creating ThreadGroup G2 ---")
    g2 = ThreadGroup("G2")
    g2.set_max_priority(10)
    
    th11_g2 = CustomThread(name="Th1", thread_group=g2, priority=4)
    th22_g2 = CustomThread(name="Th2", thread_group=g2, priority=5)
    th3_g2 = CustomThread(name="Th3", thread_group=g2, priority=5)
    
    th11_g2.start()
    th22_g2.start()
    th3_g2.start()
    
    time.sleep(0.1)
    g2.list_threads()
    
    print("\n--- Waiting for threads to complete ---")
    all_threads = [th1, th2, tha, tha_g3, thb_g3, thc_g3, thd_g3, th11_g2, th22_g2, th3_g2]
    
    for thread in all_threads:
        thread.join()
    
    print("\n=== All threads completed ===")
    
    print("\n--- Final Thread Status ---")
    sys_group.list_threads()
    g3.list_threads()
    g2.list_threads()


if __name__ == "__main__":
    main()
